package com.orctom.laputa.http.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHandler.class);

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    LOGGER.trace("------------------------");
    LOGGER.trace(msg.getClass().getName());
    LOGGER.trace("------------------------");

    if (msg instanceof DefaultHttpResponse) {
      DefaultHttpResponse response = (DefaultHttpResponse) msg;
      LOGGER.trace("CONTENT_TYPE: {}", response.headers().get(HttpHeaderNames.CONTENT_TYPE));
    }

    if (msg instanceof DefaultLastHttpContent) {
      try {
        DefaultLastHttpContent content = (DefaultLastHttpContent) msg;
        ByteBuf buf = content.content();
        LOGGER.trace(".....................");
        LOGGER.trace(buf.toString(io.netty.util.CharsetUtil.UTF_8));
        buf.release();
      } finally {
        ctx.close();
      }
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
