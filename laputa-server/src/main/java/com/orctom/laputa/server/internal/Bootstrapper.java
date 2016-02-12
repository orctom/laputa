package com.orctom.laputa.server.internal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * boot netty service
 * Created by hao on 1/6/16.
 */
public class Bootstrapper {

  private boolean useSSL = false;
  private int port = 7000;

  private SslContext sslContext;

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;

  public Bootstrapper() {
  }

  public Bootstrapper(int port) {
    this.port = port;
  }

  public Bootstrapper(int port, boolean useSSL) {
    this.port = port;
    this.useSSL = useSSL;
  }

  public void bootstrapService() throws CertificateException, SSLException, InterruptedException {
    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup();

    setupSSLContext();

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.option(ChannelOption.SO_BACKLOG, 1024);
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new LaputaServerInitializer(sslContext));

      Channel ch = b.bind(port).sync().channel();

      System.out.println("Service started " + (useSSL ? "https" : "http") + "://127.0.0.1:" + port + '/');

      ch.closeFuture().sync();
    } finally {
      shutdown();
    }
  }

  private void setupSSLContext() throws CertificateException, SSLException {
    if (useSSL) {
      SelfSignedCertificate ssc = new SelfSignedCertificate();
      sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
    }
  }

  private void shutdown() {
    System.out.println("stopping................................");
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }
}
