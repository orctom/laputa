package com.orctom.laputa.service.internal;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

class LaputaServerInitializer extends ChannelInitializer<SocketChannel> {

  private final SslContext sslContext;
  private final CorsConfig corsConfig;
  private final String webSocketPath;
  private final LaputaServerHandler handler;

  LaputaServerInitializer(SslContext sslContext,
                          CorsConfig corsConfig,
                          String webSocketPath,
                          LaputaServerHandler handler) {
    this.sslContext = sslContext;
    this.corsConfig = corsConfig;
    this.webSocketPath = webSocketPath;
    this.handler = handler;
  }

  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();
    p.addLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS));
    if (sslContext != null) {
      p.addLast(sslContext.newHandler(ch.alloc()));
    }
    p.addLast(new HttpContentCompressor(5));
    p.addLast(new HttpServerCodec());
    p.addLast(new HttpObjectAggregator(1048576));
    p.addLast(new ChunkedWriteHandler());
    if (null != corsConfig) {
      p.addLast(new CorsHandler(corsConfig));
    }
    p.addLast(new WebSocketServerCompressionHandler());
    p.addLast(new WebSocketServerProtocolHandler(webSocketPath, null, true));
    p.addLast(handler);
  }
}
