package com.orctom.laputa.http.client.util;

import com.orctom.laputa.http.client.ResponseFuture;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public abstract class Channels {

  private static final AttributeKey<Object> ATTR_DEFAULT = AttributeKey.valueOf("default");
  private static final AttributeKey<ResponseFuture> ATTR_FUTURE = AttributeKey.valueOf("future");

  public static Object getAttribute(Channel channel) {
    return channel.attr(ATTR_DEFAULT).get();
  }

  public static ResponseFuture getFutureAttribute(Channel channel) {
    return channel.attr(ATTR_FUTURE).get();
  }

  public static void setAttribute(Channel channel, Object o) {
    channel.attr(ATTR_DEFAULT).set(o);
  }

  public static void setFutureAttribute(Channel channel, ResponseFuture future) {
    channel.attr(ATTR_FUTURE).set(future);
  }
}
