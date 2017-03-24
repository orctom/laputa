package com.orctom.laputa.http.client;

import com.orctom.laputa.http.client.handler.ResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

public class HttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

  private EventLoopGroup group;

  private Bootstrap b;

  private HttpClient(HttpClientConfig httpClientConfig) {
    group = new NioEventLoopGroup(httpClientConfig.getThreads(), new HttpClientThreadFactory());
    b = new Bootstrap();
    b.group(group);
    b.channel(NioSocketChannel.class);
    b.option(ChannelOption.TCP_NODELAY, true);
    b.option(ChannelOption.SO_KEEPALIVE, true);
    b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    b.option(ChannelOption.SO_REUSEADDR, false);
    b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, httpClientConfig.getTimeout());
  }

  public static HttpClient get() {
    return new HttpClient(HttpClientConfig.DEFAULT);
  }

  public static HttpClient get(HttpClientConfig httpClientConfig) {
    return new HttpClient(httpClientConfig);
  }

  public void request(HttpMethod httpMethod,
                      URI uri,
                      Map<String, ?> headers,
                      Map<String, String> cookies) {
    b.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpClientCodec());
        p.addLast(new HttpContentDecompressor());
        p.addLast(new ChunkedWriteHandler());
        p.addLast(new ResponseHandler());
      }
    });

    try {
      ChannelFuture channelFuture = b.connect(uri.getHost(), uri.getPort());
      channelFuture.addListener(new SimpleChannelFutureListener());
      HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, uri.getRawPath());
      request.headers().set(HOST, uri.getHost());
      request.headers().set(CONNECTION, HttpHeaderValues.CLOSE);
      request.headers().set(ACCEPT_ENCODING, HttpHeaderValues.GZIP);

      setHeaders(request, headers);
      setCookies(request, cookies);

      Channel ch = channelFuture.sync().channel();
      ch.writeAndFlush(request);
      ch.closeFuture().sync();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setHeaders(HttpRequest request, Map<String, ?> headers) {
    if (null == headers || headers.isEmpty()) {
      return;
    }
    HttpHeaders httpHeaders = request.headers();
    for (Map.Entry<String, ?> entry : headers.entrySet()) {
      httpHeaders.set(entry.getKey(), entry.getValue());
    }
  }

  private void setCookies(HttpRequest request, Map<String, String> cookies) {
    if (null == cookies || cookies.isEmpty()) {
      return;
    }
    List<Cookie> cookieList = new ArrayList<>(cookies.size());
    for (Map.Entry<String, String> entry : cookies.entrySet()) {
      cookieList.add(new DefaultCookie(entry.getKey(), entry.getValue()));
    }
    request.headers().set(COOKIE, ClientCookieEncoder.STRICT.encode(cookieList));
  }

  public void shutdown() {
    if (group != null) {
      group.shutdownGracefully(0, 10, TimeUnit.SECONDS);
      if (!group.isTerminated()) {
        group.shutdownNow();
      }
    }
  }
}
