package com.orctom.laputa.http.client;

import com.google.common.base.Strings;
import com.orctom.laputa.http.client.core.ChannelExecutor;
import com.orctom.laputa.http.client.core.HttpRequestFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.COOKIE;
import static io.netty.handler.codec.http.HttpMethod.POST;

public class HttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

  private ChannelExecutor channelExecutor;
  private HttpMethod httpMethod;
  private URI uri;
  private String body;
  private Map<String, String> params;
  private Map<String, ?> headers;
  private Map<String, String> cookies;

  private HttpClient(HttpClientConfig httpClientConfig) {
    this.channelExecutor = ChannelExecutor.getInstance(httpClientConfig);
  }

  public static HttpClient create() {
    return new HttpClient(HttpClientConfig.DEFAULT);
  }

  public static HttpClient create(HttpClientConfig httpClientConfig) {
    return new HttpClient(httpClientConfig);
  }

  public HttpClient get(String uri) {
    return request(HttpMethod.GET, URI.create(uri));
  }

  public HttpClient post(String uri) {
    return request(POST, URI.create(uri));
  }

  public HttpClient delete(String uri) {
    return request(HttpMethod.DELETE, URI.create(uri));
  }

  public HttpClient put(String uri) {
    return request(HttpMethod.PUT, URI.create(uri));
  }

  private HttpClient request(HttpMethod httpMethod, URI uri) {
    this.httpMethod = httpMethod;
    this.uri = uri;
    return this;
  }

  public HttpClient withBody(String body) {
    this.body = body;
    return this;
  }

  public HttpClient withParams(Map<String, String> params) {
    this.params = params;
    return this;
  }

  public HttpClient withHeaders(Map<String, ?> headers) {
    this.headers = headers;
    return this;
  }

  public HttpClient withCookies(Map<String, String> cookies) {
    this.cookies = cookies;
    return this;
  }

  public ResponseFuture execute() {
    URI transformedUri = transformedUri();
    DefaultFullHttpRequest request = HttpRequestFactory.create(httpMethod, transformedUri);
    setBody(request);
    setParams(request, transformedUri);
    setHeaders(request);
    setCookies(request);
    return channelExecutor.execute(request, uri.getHost(), uri.getPort());
  }

  private URI transformedUri() {
    if (null == params || params.isEmpty()) {
      return uri;
    }

    QueryStringEncoder encoder = new QueryStringEncoder(uri.toString());
    for (Map.Entry<String, String> entry : params.entrySet()) {
      encoder.addParam(entry.getKey(), entry.getValue());
    }
    return URI.create(encoder.toString());
  }

  private void setBody(DefaultFullHttpRequest request) {
    if (POST != httpMethod) {
      return;
    }
    if (Strings.isNullOrEmpty(body)) {
      return;
    }

    setRequestContent(request, body);
  }

  private void setParams(DefaultFullHttpRequest request, URI uri) {
    if (POST != httpMethod) {
      return;
    }
    String query = uri.getQuery();
    if (Strings.isNullOrEmpty(query)) {
      return;
    }

    setRequestContent(request, query);
  }

  private void setRequestContent(DefaultFullHttpRequest request, String content) {
    ByteBuf byteBuf = Unpooled.copiedBuffer(content, StandardCharsets.UTF_8);
    request.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
    request.content().clear().writeBytes(byteBuf);
  }

  private void setHeaders(HttpRequest request) {
    if (null == headers || headers.isEmpty()) {
      return;
    }
    HttpHeaders httpHeaders = request.headers();
    for (Map.Entry<String, ?> entry : headers.entrySet()) {
      httpHeaders.set(entry.getKey(), entry.getValue());
    }
  }

  private void setCookies(HttpRequest request) {
    if (null == cookies || cookies.isEmpty()) {
      return;
    }
    List<Cookie> cookieList = new ArrayList<>(cookies.size());
    for (Map.Entry<String, String> entry : cookies.entrySet()) {
      cookieList.add(new DefaultCookie(entry.getKey(), entry.getValue()));
    }
    request.headers().set(COOKIE, ClientCookieEncoder.STRICT.encode(cookieList));
  }
}
