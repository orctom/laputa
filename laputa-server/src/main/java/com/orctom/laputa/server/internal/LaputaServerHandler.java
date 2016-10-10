package com.orctom.laputa.server.internal;

import com.orctom.laputa.server.config.Configurator;
import com.orctom.laputa.server.model.Response;
import com.orctom.laputa.server.processor.RequestProcessor;
import com.orctom.laputa.server.processor.impl.DefaultRequestProcessor;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class LaputaServerHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaputaServerHandler.class);

  private RequestProcessor requestProcessor = new DefaultRequestProcessor();

  static {
    String staticFilesDir = Configurator.getInstance().getStaticFilesDir();

    DiskFileUpload.deleteOnExitTemporaryFile = true;
    DiskFileUpload.baseDirectory = staticFilesDir;
    DiskAttribute.deleteOnExitTemporaryFile = true;
    DiskAttribute.baseDirectory = staticFilesDir;
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof HttpRequest) {
      HttpRequest req = (HttpRequest) msg;

      if (HttpUtil.is100ContinueExpected(req)) {
        ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
      }

      boolean keepAlive = HttpUtil.isKeepAlive(req);

      Response response = requestProcessor.handleRequest(req);

      FullHttpResponse res = new DefaultFullHttpResponse(
          HTTP_1_1,
          OK,
          Unpooled.wrappedBuffer(response.getContent())
      );
      res.headers().set(CONTENT_TYPE, response.getMediaType());
      res.headers().set(CONTENT_LENGTH, res.content().readableBytes());

      if (!keepAlive) {
        ctx.write(res).addListener(ChannelFutureListener.CLOSE);
      } else {
        res.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.write(res);
      }
    } else {
      ctx.writeAndFlush(HttpResponseStatus.NO_CONTENT);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    LOGGER.error(cause.getMessage(), cause);
    ctx.close();
  }
}
