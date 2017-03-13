package com.orctom.laputa.service.internal;

import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.handler.ServiceHandler;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.typesafe.config.Config;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.ReadTimeoutException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ServiceLoader;

import static com.orctom.laputa.service.Constants.*;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class LaputaServerHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaputaServerHandler.class);

  private static final int MAX_FRAME_PAYLOAD_LENGTH = 5 * 1024 * 1024;

  private static LaputaRequestProcessor requestProcessor = new LaputaRequestProcessor();

  private static boolean isUseSSL = false;

  private static String webSocketPath;
  private WebSocketServerHandshaker handshaker;

  static {
    Config config = Configurator.getInstance().getConfig();
    String uploadDir = config.getString(CFG_UPLOAD_DIR);

    DiskFileUpload.deleteOnExitTemporaryFile = true;
    DiskFileUpload.baseDirectory = uploadDir;
    DiskAttribute.deleteOnExitTemporaryFile = true;
    DiskAttribute.baseDirectory = uploadDir;

    webSocketPath = config.getString(CFG_WEBSOCKET_PATH);
  }

  public LaputaServerHandler(boolean isUseSSL) {
    LaputaServerHandler.isUseSSL = isUseSSL;
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf byteBuf = null;
    if (msg instanceof ByteBufHolder) {
      byteBuf = ((ByteBufHolder) msg).content();
    }

    try {
      if (msg instanceof FullHttpRequest) {
        handleHttpRequest(ctx, (FullHttpRequest) msg);

      } else if (msg instanceof WebSocketFrame) {
        handleWebSocketFrame(ctx, (WebSocketFrame) msg);

      } else {
        ctx.writeAndFlush(HttpResponseStatus.NO_CONTENT);
      }
    } catch (Exception e) {
      ctx.writeAndFlush(HttpResponseStatus.INTERNAL_SERVER_ERROR);
      LOGGER.error(e.getMessage(), e);
    } finally {
      if (null != byteBuf) {
        byteBuf.release();
      }
    }
  }

  private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
    if (HttpUtil.is100ContinueExpected(req)) {
      ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
    }

    if (webSocketPath.equals(req.uri())) {
      WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
          getWebSocketLocation(req), null, true, MAX_FRAME_PAYLOAD_LENGTH
      );
      handshaker = wsFactory.newHandshaker(req);
      if (handshaker == null) {
        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
      } else {
        handshaker.handshake(ctx.channel(), req);
      }
      return;
    }

    ResponseWrapper responseWrapper = requestProcessor.handleRequest(req);

    if (responseWrapper.getStatus().code() >= 400) {
      sendError(ctx, req, responseWrapper);
      return;
    }

    if (isRedirectionResponse(responseWrapper)) {
      redirectionResponse(ctx, req, responseWrapper);
      return;
    }

    ServiceLoader<ServiceHandler> serviceLoaders = ServiceLoader.load(ServiceHandler.class);

    for (ServiceHandler serviceHandler : serviceLoaders) {
      if (serviceHandler.handle(ctx, req, responseWrapper)) {
        return;
      }
    }

    response(ctx, req, responseWrapper);
  }

  private void sendError(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    FullHttpResponse res = getHttpResponse(responseWrapper);
    res.headers().set(HttpHeaderNames.CONTENT_TYPE, MediaType.TEXT_PLAIN.getValue());
    setNoCacheHeader(res);
    writeResponse(ctx, req, res, responseWrapper.getStatus());
  }

  private boolean isRedirectionResponse(ResponseWrapper responseWrapper) {
    String redirectTo = responseWrapper.getRedirectTo();
    return null != redirectTo && redirectTo.trim().length() > 0;
  }

  private void redirectionResponse(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    HttpResponseStatus status = responseWrapper.isPermanentRedirect() ? MOVED_PERMANENTLY : FOUND;
    FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, status);
    res.headers().set(LOCATION, responseWrapper.getRedirectTo());
    setNoCacheHeader(res);
    writeResponse(ctx, req, res, responseWrapper.getStatus());
  }

  private void setNoCacheHeader(FullHttpResponse res) {
    res.headers().set(CACHE_CONTROL, HEADER_CACHE_CONTROL_NO_CACHE);
    res.headers().set(EXPIRES, HEADER_EXPIRE_NOW);
  }

  private void response(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    FullHttpResponse res = getHttpResponse(responseWrapper);
    res.headers().set(CONTENT_TYPE, responseWrapper.getMediaType());
    writeResponse(ctx, req, res, responseWrapper.getStatus());
  }

  private FullHttpResponse getHttpResponse(ResponseWrapper responseWrapper) {
    if (null == responseWrapper.getContent()) {
      return new DefaultFullHttpResponse(HTTP_1_1, responseWrapper.getStatus());
    }

    return new DefaultFullHttpResponse(
        HTTP_1_1,
        responseWrapper.getStatus(),
        Unpooled.wrappedBuffer(responseWrapper.getContent())
    );
  }

  private void writeResponse(ChannelHandlerContext ctx,
                             FullHttpRequest req,
                             FullHttpResponse res,
                             HttpResponseStatus status) {
    setDateHeader(req, res, status);
    if (!HttpUtil.isContentLengthSet(res)) {
      HttpUtil.setContentLength(res, res.content().readableBytes());
    }

    boolean keepAlive = HttpUtil.isKeepAlive(req);
    if (keepAlive) {
      res.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      ctx.write(res);
    } else {
      ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
    }
  }

  private void setDateHeader(FullHttpRequest req, FullHttpResponse res, HttpResponseStatus status) {
    String date = req.headers().get(IF_MODIFIED_SINCE);
    if (NOT_MODIFIED != status) {
      date = DateTime.now().toString(HTTP_DATE_FORMATTER);
    }
    res.headers().set(DATE, date);
    res.headers().set(LAST_MODIFIED, date);
  }

  private static String getWebSocketLocation(FullHttpRequest req) {
    String location = req.headers().get(HttpHeaderNames.HOST) + webSocketPath;
    if (isUseSSL) {
      return "wss://" + location;
    } else {
      return "ws://" + location;
    }
  }

  private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

    // Check for closing frame
    if (frame instanceof CloseWebSocketFrame) {
      handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
      return;
    }
    if (frame instanceof PingWebSocketFrame) {
      ctx.write(new PongWebSocketFrame(frame.content().retain()));
      return;
    }
    if (frame instanceof TextWebSocketFrame) {
      // Echo the frame
      ctx.write(frame.retain());
      return;
    }
    if (frame instanceof BinaryWebSocketFrame) {
      // Echo the frame
      ctx.write(frame.retain());
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    ctx.close();
    if (cause instanceof IOException ||
        cause instanceof ReadTimeoutException) {
      return;
    }
    LOGGER.error(cause.getMessage(), cause);
  }
}
