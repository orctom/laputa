package com.orctom.laputa.http.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;

public interface AsyncHandler {

  void handleHeader(HttpHeaders headers);

  void handleContent(ByteBuf content);

  void handleLastContent(ByteBuf content);
}
