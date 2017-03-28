package com.orctom.laputa.http.client.core;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpMethod.POST;

public abstract class HttpRequestFactory {

  private static final String SIGN_QUESTION = "?";

  public static DefaultFullHttpRequest create(HttpMethod httpMethod, URI uri) {
    String path = getPath(httpMethod, uri);
    DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, path);
    request.headers().set(HOST, uri.getHost());
    request.headers().set(CONNECTION, HttpHeaderValues.CLOSE);
    request.headers().set(ACCEPT_ENCODING, HttpHeaderValues.GZIP);
    if (POST == httpMethod) {
      request.headers().set(CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
    }
    return request;
  }

  private static String getPath(HttpMethod httpMethod, URI uri) {
    if (POST == httpMethod) {
      return uri.getPath();
    }

    return uri.getPath() + SIGN_QUESTION + uri.getQuery();
  }
}
