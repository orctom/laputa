package com.orctom.laputa.http.client;

import com.orctom.laputa.http.client.handler.AsyncHandler;
import com.orctom.laputa.http.client.handler.DefaultAsyncHandler;

import java.util.concurrent.CompletableFuture;

public class ResponseFuture extends CompletableFuture<Response> {

  private AsyncHandler handler;

  public ResponseFuture() {
    this.handler = new DefaultAsyncHandler(this);
  }

  public AsyncHandler getHandler() {
    return handler;
  }
}
