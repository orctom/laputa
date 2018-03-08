package com.orctom.laputa.service.model;

import com.google.common.base.Strings;
import com.orctom.laputa.service.util.PathUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.orctom.laputa.service.Constants.UTF_8;

/**
 * Request Wrapper holding the path and the params translated from query string
 * Created by chenhao on 9/27/16.
 */
public class RequestWrapper {

  private HttpMethod httpMethod;
  private HttpHeaders headers;
  private String uri;
  private String path;
  private Map<String, List<String>> params;
  private String data;
  private Map<String, String> cookies;

  public RequestWrapper(
      HttpMethod httpMethod,
      HttpHeaders headers,
      String uri,
      String path,
      Map<String, List<String>> params,
      String data) {
    this.httpMethod = httpMethod;
    this.headers = headers;
    this.uri = decode(uri);
    this.path = PathUtils.removeDuplicatedSlashes(decode(path));
    this.params = params;
    this.data = decode(data);
    initCookies();
  }

  private String decode(String raw) {
    if (null == raw || raw.startsWith("--")) {
      return raw;
    }

    try {
      return URLDecoder.decode(raw, UTF_8);
    } catch (UnsupportedEncodingException | IllegalArgumentException e) {
      return raw;
    }
  }

  private void initCookies() {
    if (null == headers || headers.isEmpty()) {
      return;
    }
    String value = headers.get(HttpHeaderNames.COOKIE);
    if (Strings.isNullOrEmpty(value)) {
      return;
    }
    Set<Cookie> cookieSet = ServerCookieDecoder.STRICT.decode(value);
    this.cookies = cookieSet.stream().collect(Collectors.toMap(Cookie::name, Cookie::value));
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = PathUtils.removeDuplicatedSlashes(path);
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public Map<String, List<String>> getParams() {
    return params;
  }

  public void setParams(Map<String, List<String>> params) {
    this.params = params;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public Map<String, String> getCookies() {
    return cookies;
  }

  @Override
  public String toString() {
    return "path: " + path + ", params: " + params;
  }
}
