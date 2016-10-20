package com.orctom.laputa.service.model;

public enum HTTPMethod {

  GET("@get"),
  POST("@post"),
  PUT("@put"),
  DELETE("@delete"),
  HEAD("@head"),
  OPTIONS("@options");

  private String key;

  HTTPMethod(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
