package com.orctom.laputa.server.internal;

import com.orctom.exception.IllegalConfigException;
import com.orctom.laputa.server.config.ServiceConfig;
import com.orctom.utils.HostUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.File;
import java.security.cert.CertificateException;

/**
 * boot netty service
 * Created by hao on 1/6/16.
 */
public class Bootstrapper extends Thread {

  private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrapper.class);

  private int port;
  private boolean useSSL;

  private SslContext sslContext;

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;

  public Bootstrapper(int port, boolean useSSL) {
    this.port = port;
    this.useSSL = useSSL;
  }

  @Override
  public void run() {
    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup();

    try {
      setupSSLContext();
      ServerBootstrap b = new ServerBootstrap();
      b.option(ChannelOption.SO_BACKLOG, 1024);
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new LaputaServerInitializer(sslContext));

      Channel ch = b.bind(port).sync().channel();

      String ip = HostUtils.getIP();
      LOGGER.warn("Service started {}{}:{}", (useSSL ? "https://" : "http://"), ip, port);

      ch.closeFuture().sync();
    } catch (IllegalConfigException e) {
      LOGGER.error(e.getMessage());
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      shutdown();
    }
  }

  private void setupSSLContext() throws CertificateException, SSLException {
    if (useSSL) {
      File certificate = new File(getConfigAsString("server.https.certificate"));
      assertFileExist(certificate, "Failed to find certificate.");

      File privateKey = new File(getConfigAsString("server.https.privateKey"));
      assertFileExist(privateKey, "Failed to find private key.");
      sslContext = SslContextBuilder.forServer(certificate, privateKey).build();
    }
  }

  private String getConfigAsString(String key) {
    try {
      return ServiceConfig.getInstance().getConfig().getString(key);
    } catch (Exception e) {
      throw new IllegalConfigException(e.getMessage());
    }
  }

  private void assertFileExist(File file, String message) {
    if (!file.exists()) {
      throw new IllegalArgumentException(message + " Path: " + file.getAbsolutePath());
    }
  }

  private void shutdown() {
    LOGGER.warn("shutting down {}...", useSSL ? "https" : "http");
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }
}
