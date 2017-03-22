package com.orctom.laputa.http.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleChannelFutureListener implements ChannelFutureListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleChannelFutureListener.class);

  @Override
  public void operationComplete(ChannelFuture future) throws Exception {
    Channel channel = future.channel();
    if (future.isSuccess()) {
      onSuccess(channel);
    } else {
      onFailure(channel, future.cause());
    }
  }

  protected void onSuccess(Channel channel) {
    LOGGER.trace("Connected.");
  }

  protected void onFailure(Channel channel, Throwable t) {
    LOGGER.error(t.getMessage(), t);
  }
}
