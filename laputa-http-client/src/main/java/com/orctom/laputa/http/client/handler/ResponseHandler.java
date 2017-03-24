package com.orctom.laputa.http.client.handler;

import com.orctom.laputa.http.client.Channels;
import com.orctom.laputa.http.client.ResponseFuture;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    Channel channel = ctx.channel();
    ResponseFuture future = Channels.getFutureAttribute(channel);

    if (future.isDone()) {
      return;
    }

    if (msg instanceof HttpResponse) {
      HttpResponse response = (HttpResponse) msg;
      HttpHeaders httpHeaders = response.headers();
      Map<String, String> headers = new HashMap<>(httpHeaders.size());
      for (Map.Entry<String, String> entry : httpHeaders) {
        headers.put(entry.getKey(), entry.getValue());
      }
    }

    if (msg instanceof HttpContent) {
      try {
        HttpContent content = (HttpContent) msg;
        ByteBuf buf = content.content();
        LOGGER.trace(buf.toString(io.netty.util.CharsetUtil.UTF_8));
        buf.release();
      } finally {
        ctx.close();
      }
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    LOGGER.error(cause.getMessage(), cause);
    ctx.close();
  }
}
