package com.orctom.laputa.http.client.core;

import com.orctom.laputa.http.client.ResponseFuture;
import com.orctom.laputa.http.client.handler.AsyncHandler;
import com.orctom.laputa.http.client.util.Channels;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    Channel channel = ctx.channel();
    ResponseFuture future = Channels.getFutureAttribute(channel);

    if (future.isDone()) {
      return;
    }

    AsyncHandler handler = future.getHandler();
    if (msg instanceof HttpResponse) {
      HttpResponse response = (HttpResponse) msg;
      handler.handleHeader(response.headers());
      return;
    }

    if (msg instanceof LastHttpContent) {
      LastHttpContent lastHttpContent = (LastHttpContent) msg;
      handler.handleLastContent(lastHttpContent.content());
      ctx.close();
      return;
    }

    if (msg instanceof HttpContent) {
      HttpContent content = (HttpContent) msg;
      handler.handleContent(content.content());
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    LOGGER.error(cause.getMessage(), cause);
    ctx.close();
  }
}
