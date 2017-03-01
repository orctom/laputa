package com.orctom.laputa.service.internal;

import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.exception.RequestProcessingException;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.processor.RequestProcessor;
import com.orctom.laputa.service.processor.impl.DefaultRequestProcessor;
import com.typesafe.config.Config;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.timeout.ReadTimeoutException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

import static com.orctom.laputa.service.Constants.*;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class LaputaServerHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaputaServerHandler.class);

  private static final int MAX_FRAME_PAYLOAD_LENGTH = 5 * 1024 * 1024;

  private static RequestProcessor requestProcessor = new DefaultRequestProcessor();

  private static boolean isUseSSL = false;

  private static String websocketPath;
  private WebSocketServerHandshaker handshaker;

  static {
    Config config = Configurator.getInstance().getConfig();
    String uploadDir = config.getString(CFG_UPLOAD_DIR);

    DiskFileUpload.deleteOnExitTemporaryFile = true;
    DiskFileUpload.baseDirectory = uploadDir;
    DiskAttribute.deleteOnExitTemporaryFile = true;
    DiskAttribute.baseDirectory = uploadDir;

    websocketPath = config.getString(CFG_WEBSOCKET_PATH);
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


    if (websocketPath.equals(req.uri())) {
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

    if (isRedirectionResponse(responseWrapper)) {
      redirectionResponse(ctx, req, responseWrapper);
      return;
    }

    if (isStaticFileResponse(responseWrapper)) {
      staticFileResponse(ctx, req, responseWrapper);
      return;
    }

    response(ctx, req, responseWrapper);
  }

  private boolean isRedirectionResponse(ResponseWrapper responseWrapper) {
    String redirectTo = responseWrapper.getRedirectTo();
    return null != redirectTo && redirectTo.trim().length() > 0;
  }

  private void redirectionResponse(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    HttpResponseStatus status = responseWrapper.isPermanentRedirect() ? MOVED_PERMANENTLY : FOUND;
    FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, status);
    res.headers().set(LOCATION, responseWrapper.getRedirectTo());
    res.headers().set(CACHE_CONTROL, "max-age=0");
    writeResponse(ctx, req, res);
  }

  private boolean isStaticFileResponse(ResponseWrapper responseWrapper) {
    return null != responseWrapper.getFile();
  }

  private void staticFileResponse(ChannelHandlerContext ctx,
                                  FullHttpRequest req,
                                  ResponseWrapper responseWrapper) {
    try {
      RandomAccessFile file = responseWrapper.getFile();
      HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
      long contentLength = file.length();
      res.headers().set(CONTENT_LENGTH, contentLength);

      // Write the content.
      ChannelFuture lastContentFuture;
      if (null == ctx.pipeline().get(SslHandler.class)) {
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, contentLength), ctx.newProgressivePromise());
        lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

      } else {
        lastContentFuture =
            ctx.writeAndFlush(
                new HttpChunkedInput(new ChunkedFile(file, 0, contentLength, 8192)),
                ctx.newProgressivePromise()
            );
      }

      if (!HttpUtil.isKeepAlive(req)) {
        lastContentFuture.addListener(ChannelFutureListener.CLOSE);
      }
    } catch (IOException e) {
      throw new RequestProcessingException(e.getMessage(), e);
    }
  }

  private void response(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    FullHttpResponse res = new DefaultFullHttpResponse(
        HTTP_1_1,
        responseWrapper.getStatus(),
        Unpooled.wrappedBuffer(responseWrapper.getContent())
    );
    res.headers().set(CONTENT_TYPE, responseWrapper.getMediaType());
    res.headers().set(CONTENT_LENGTH, res.content().readableBytes());
    res.headers().set(DATE, DateTime.now().toString(HTTP_DATE_FORMATTER));
    writeResponse(ctx, req, res);
  }

  private void writeResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
    boolean keepAlive = HttpUtil.isKeepAlive(req);
    if (keepAlive) {
      res.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      ctx.write(res);
    } else {
      ctx.write(res).addListener(ChannelFutureListener.CLOSE);
    }
  }

  private static String getWebSocketLocation(FullHttpRequest req) {
    String location = req.headers().get(HttpHeaderNames.HOST) + websocketPath;
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
