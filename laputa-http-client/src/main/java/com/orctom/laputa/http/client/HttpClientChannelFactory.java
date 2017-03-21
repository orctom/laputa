package com.orctom.laputa.http.client;

import io.netty.channel.ChannelFactory;
import io.netty.channel.socket.nio.NioSocketChannel;

public class HttpClientChannelFactory implements ChannelFactory<NioSocketChannel> {

  @Override
  public NioSocketChannel newChannel() {
    return new NioSocketChannel();
  }
}
