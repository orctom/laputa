package com.orctom.test;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Created by hao on 9/5/15.
 */
public class HttpChannelInitializer extends ChannelInitializer<Channel> {
	private final boolean client;

	public HttpChannelInitializer(boolean client) {
		this.client = client;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new TimeServerHandler());
		if (client) {
			pipeline.addLast("decoder", new HttpResponseDecoder());        //1
			pipeline.addLast("encoder", new HttpRequestEncoder());        //2
		} else {
			pipeline.addLast("decoder", new HttpRequestDecoder());        //3
			pipeline.addLast("encoder", new HttpResponseEncoder());        //4
		}
	}
}
