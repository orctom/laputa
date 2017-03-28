package com.orctom.laputa.http.client.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChannelInitializationHandler extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();
    p.addLast(new HttpClientCodec());
    p.addLast(new HttpContentDecompressor());
    p.addLast(new ChunkedWriteHandler());
    p.addLast(new ResponseHandler());
  }
}
