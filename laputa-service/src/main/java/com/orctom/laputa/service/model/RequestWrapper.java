package com.orctom.laputa.service.model;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import static com.orctom.laputa.service.Constants.UTF_8;

/**
 * Request Wrapper holding the path and the params translated from query string
 * Created by chenhao on 9/27/16.
 */
public class RequestWrapper {

  private HttpMethod httpMethod;
  private HttpHeaders headers;
  private String path;
  private Map<String, List<String>> params;
  private String data;

  public RequestWrapper(
      HttpMethod httpMethod,
      HttpHeaders headers,
      String path,
      Map<String, List<String>> params,
      String data) {
    this.httpMethod = httpMethod;
    this.headers = headers;
    this.path = decode(path);
    this.params = params;
    this.data = decode(data);
  }

  private String decode(String raw) {
    try {
      return URLDecoder.decode(raw, UTF_8);
    } catch (UnsupportedEncodingException e) {
      return raw;
    }
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
    this.path = path;
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

  @Override
  public String toString() {
    return "path: " + path + ", params: " + params;
  }
}
