package com.orctom.laputa.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

import static java.util.concurrent.TimeUnit.SECONDS;

public class HttpClient implements Closeable {

  private static EventLoopGroup group;

  public static HttpClient get() {
    group = new NioEventLoopGroup();
    Bootstrap b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpClientCodec());
        p.addLast(new HttpContentDecompressor());
        p.addLast(new HttpClientHandler());
      }
    });
    b.option(ChannelOption.TCP_NODELAY, true);
    b.option(ChannelOption.SO_KEEPALIVE, true);
    b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    b.option(ChannelOption.SO_REUSEADDR, false);
    b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
  }

  public void request(URI uri) throws InterruptedException {
    try {
      ChannelFuture channelFuture = b.connect(uri.getHost(), uri.getPort());
      channelFuture.addListener(new SimpleChannelFutureListener());
      HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
      request.headers().set(HttpHeaderNames.HOST, uri.getHost());
      request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
      request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

      // Set some example cookies.
      request.headers().set(
          HttpHeaderNames.COOKIE,
          ClientCookieEncoder.STRICT.encode(
              new DefaultCookie("my-cookie", "foo"),
              new DefaultCookie("another-cookie", "bar")));

      Channel ch = channelFuture.sync().channel();
      ch.writeAndFlush(request);
      ch.closeFuture().sync();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() throws IOException {
    if (group != null) {
      group.shutdownGracefully(0, 10, SECONDS);
      if (!group.isTerminated()) {
        group.shutdownNow();
      }
    }
  }
}
