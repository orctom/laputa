package com.orctom.laputa.service.internal;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

class LaputaServerInitializer extends ChannelInitializer<SocketChannel> {

  private final SslContext sslContext;

  LaputaServerInitializer(SslContext sslContext) {
    this.sslContext = sslContext;
  }

  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();
    p.addLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS));
    if (sslContext != null) {
      p.addLast(sslContext.newHandler(ch.alloc()));
    }
    p.addLast(new HttpServerCodec());
    p.addLast(new HttpObjectAggregator(1048576));
    p.addLast(new HttpContentCompressor(1));
    p.addLast(new LaputaServerHandler(null != sslContext));
  }
}
