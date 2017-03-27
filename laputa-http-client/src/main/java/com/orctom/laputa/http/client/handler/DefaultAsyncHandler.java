package com.orctom.laputa.http.client.handler;

import com.orctom.laputa.http.client.Response;
import com.orctom.laputa.http.client.ResponseFuture;
import com.orctom.laputa.http.client.util.ByteBufs;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

public class DefaultAsyncHandler implements AsyncHandler {

  private ResponseFuture responseFuture;
  private Response response = new Response();

  public DefaultAsyncHandler(ResponseFuture responseFuture) {
    this.responseFuture = responseFuture;
  }

  @Override
  public void handleHeader(HttpHeaders httpHeaders) {
    Map<String, String> headers = new HashMap<>(httpHeaders.size());
    for (Map.Entry<String, String> entry : httpHeaders) {
      headers.put(entry.getKey(), entry.getValue());
    }
    response.setHeaders(headers);
  }

  @Override
  public void handleContent(ByteBuf content) {
    response.appendContent(ByteBufs.toByteArray(content));
  }

  @Override
  public void handleLastContent(ByteBuf content) {
    try {
      response.appendContent(ByteBufs.toByteArray(content));
    } finally {
      content.release();
    }
    responseFuture.complete(response);
  }
}
