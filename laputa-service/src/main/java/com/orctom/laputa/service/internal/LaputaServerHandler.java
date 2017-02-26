package com.orctom.laputa.service.internal;

import com.google.common.base.Strings;
import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.processor.RequestProcessor;
import com.orctom.laputa.service.processor.impl.DefaultRequestProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class LaputaServerHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaputaServerHandler.class);

  private static final String WEBSOCKET_PATH = "/websocket";

  private static RequestProcessor requestProcessor = new DefaultRequestProcessor();

  private static boolean isUseSSL = false;

  private WebSocketServerHandshaker handshaker;

  static {
    String staticFilesDir = Configurator.getInstance().getStaticFilesDir();

    DiskFileUpload.deleteOnExitTemporaryFile = true;
    DiskFileUpload.baseDirectory = staticFilesDir;
    DiskAttribute.deleteOnExitTemporaryFile = true;
    DiskAttribute.baseDirectory = staticFilesDir;
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

    boolean keepAlive = HttpUtil.isKeepAlive(req);

    if ("/websocket".equals(req.uri())) {
      WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
          getWebSocketLocation(req),
          null,
          true,
          5 * 1024 * 1024
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

    FullHttpResponse res;
    if (Strings.isNullOrEmpty(responseWrapper.getRedirectTo())) {
      res = new DefaultFullHttpResponse(
          HTTP_1_1,
          OK,
          Unpooled.wrappedBuffer(responseWrapper.getContent())
      );
      res.headers().set(CONTENT_TYPE, responseWrapper.getMediaType());
      res.headers().set(CONTENT_LENGTH, res.content().readableBytes());
    } else {
      HttpResponseStatus status = responseWrapper.isPermanentRedirect() ? MOVED_PERMANENTLY : FOUND;
      res = new DefaultFullHttpResponse(HTTP_1_1, status);
      res.headers().set(LOCATION, responseWrapper.getRedirectTo());
      res.headers().set(CACHE_CONTROL, "max-age=0");
    }

    if (!keepAlive) {
      ctx.write(res).addListener(ChannelFutureListener.CLOSE);
    } else {
      res.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      ctx.write(res);
    }
  }

  private static String getWebSocketLocation(FullHttpRequest req) {
    String location =  req.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_PATH;
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
    if (!(cause instanceof ReadTimeoutException)) {
      LOGGER.error(cause.getMessage(), cause);
    }
    ctx.close();
  }
}
