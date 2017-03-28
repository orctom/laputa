package com.orctom.laputa.http.client.core;

import com.orctom.laputa.http.client.HttpClientConfig;
import com.orctom.laputa.http.client.ResponseFuture;
import com.orctom.laputa.http.client.util.Channels;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class ChannelExecutor {

  private static Map<HttpClientConfig, ChannelExecutor> executorMap = new WeakHashMap<>();

  private EventLoopGroup group;

  private Bootstrap b;

  private ChannelExecutor(HttpClientConfig httpClientConfig) {
    group = new NioEventLoopGroup(httpClientConfig.getThreads(), new HttpClientThreadFactory());
    b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.option(ChannelOption.TCP_NODELAY, true);
    b.option(ChannelOption.SO_KEEPALIVE, true);
    b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    b.option(ChannelOption.SO_REUSEADDR, false);
    b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, httpClientConfig.getTimeout());
    b.handler(new ChannelInitializationHandler());

    Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
  }

  public static ChannelExecutor getInstance(HttpClientConfig httpClientConfig) {
    return executorMap.computeIfAbsent(httpClientConfig, ChannelExecutor::new);
  }

  public ResponseFuture execute(HttpRequest request, String host, int port) {
    ResponseFuture responseFuture = new ResponseFuture();
    try {
      ChannelFuture channelFuture = b.connect(host, port);
      channelFuture.addListener(new SimpleChannelFutureListener());
      Channel ch = channelFuture.sync().channel();
      Channels.setFutureAttribute(ch, responseFuture);
      ch.writeAndFlush(request);
    } catch (Exception e) {
      responseFuture.completeExceptionally(e);
    }

    return responseFuture;
  }

  private void shutdown() {
    if (group != null) {
      group.shutdownGracefully(0, 10, TimeUnit.SECONDS);
    }
  }
}
