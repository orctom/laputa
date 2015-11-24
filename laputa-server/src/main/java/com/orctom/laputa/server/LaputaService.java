package com.orctom.laputa.server;

import com.orctom.laputa.server.config.MappingConfig;
import com.orctom.laputa.server.internal.BeanFactory;
import com.orctom.laputa.util.exception.ClassLoadingException;
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
import java.lang.annotation.Annotation;
import java.security.cert.CertificateException;

/**
 * Serving http
 * Created by hao on 9/10/15.
 */
public class LaputaService {

	private boolean ssl = false;
	private int port = 7000;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private SslContext sslContext;

	private String[] basePackages;
	private Class<? extends Annotation> annotation;
	private BeanFactory beanFactory;

	public LaputaService() {
	}

	public LaputaService(int port) {
		this.port = port;
	}

	public LaputaService(boolean ssl, int port) {
		this.ssl = ssl;
		this.port = port;
	}

	public LaputaService scanPackage(String... basePackages) {
		this.basePackages = basePackages;
		return this;
	}

	public LaputaService forAnnotation(Class<? extends Annotation> annotation) {
		this.annotation = annotation;
		return this;
	}

	public LaputaService beanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		return this;
	}

	public void startup() throws Exception {
		validate();
		bootstrapMapper();
		bootstrapService();
	}

	private void validate() {
		if (null == basePackages) {
			throw new IllegalStateException("'base packages' not set");
		}

		if (null == annotation) {
			throw new IllegalStateException("'annotation' to scan not set");
		}

//		if (null == beanFactory) {
//			throw new IllegalStateException("'bean factory' to scan not set");
//		}
	}

	private void bootstrapMapper() throws ClassLoadingException {
		MappingConfig.getInstance().scan(annotation, basePackages);
	}

	private void bootstrapService() throws CertificateException, SSLException, InterruptedException {
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

			System.out.println("Service started " + (ssl ? "https" : "http") + "://127.0.0.1:" + port + '/');

			ch.closeFuture().sync();
		} finally {
			shutdown();
		}
	}

	private void setupSSLContext() throws CertificateException, SSLException {
		if (ssl) {
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
