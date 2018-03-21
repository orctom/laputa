package com.orctom.laputa.service.internal;

import com.orctom.laputa.service.config.Configurator;
import com.typesafe.config.Config;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.orctom.laputa.service.Constants.CFG_UPLOAD_DIR;
import static com.orctom.laputa.service.Constants.CFG_WEBSOCKET_PATH;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class LaputaServerHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaputaServerHandler.class);

  private static final int MAX_FRAME_PAYLOAD_LENGTH = 5 * 1024 * 1024;

  private static String webSocketPath;

  private final boolean isUseSSL;
  private final LaputaRequestProcessor requestProcessor;

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

  public LaputaServerHandler(boolean isUseSSL, LaputaRequestProcessor requestProcessor) {
    this.isUseSSL = isUseSSL;
    this.requestProcessor = requestProcessor;
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

    requestProcessor.handleRequest(ctx, req);
  }

  private String getWebSocketLocation(FullHttpRequest req) {
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
      String request = ((TextWebSocketFrame) frame).text();
      System.out.println("Message from " + ctx.channel() + ": " + request);
      ctx.write(frame.retain());
      return;
    }
    if (frame instanceof BinaryWebSocketFrame) {
      // Echo the frame
      BinaryWebSocketFrame msg = (BinaryWebSocketFrame) frame;
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
