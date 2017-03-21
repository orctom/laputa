package com.orctom.laputa.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import java.io.Closeable;
import java.io.IOException;

public class HttpClient implements Closeable {

  private static final String HOST = "localhost";
  private int port;

  private Bootstrap b;
  private EventLoopGroup group;

  public HttpClient(int port) {
    this.port = port;
    group = new NioEventLoopGroup();
    b = new Bootstrap();
    b.group(group);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpClientHandler());
      }
    });
    b.option(ChannelOption.TCP_NODELAY, true);
    b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    b.option(ChannelOption.SO_REUSEADDR, false);
    b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
    b.channelFactory(new HttpClientChannelFactory());
  }

  public void start() {
    try {
      ChannelFuture f = b.connect(HOST, port).sync();
      f.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() throws IOException {
    b.clone();
    group.shutdownGracefully();
  }
}
