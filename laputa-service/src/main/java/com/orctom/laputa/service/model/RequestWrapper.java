package com.orctom.laputa.service.model;

import io.netty.handler.codec.http.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * Request Wrapper holding the path and the params translated from query string
 * Created by chenhao on 9/27/16.
 */
public class RequestWrapper {

  private HttpMethod httpMethod;
  private String path;
  private Map<String, List<String>> params;
  private String data;

  public RequestWrapper(
      HttpMethod httpMethod,
      String path,
      Map<String, List<String>> params,
      String data) {
    this.httpMethod = httpMethod;
    this.path = path;
    this.params = params;
    this.data = data;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
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
