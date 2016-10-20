package com.orctom.laputa.service.model;

import java.util.List;

public class Response<T> {

  private int code = 200;
  private T data;
  private List<String> messages;

  public Response() {
  }

  public Response(T data) {
    this.data = data;
  }

  public Response(List<String> messages) {
    this.code = 400;
    this.messages = messages;
  }

  public Response(int code, List<String> messages) {
    this.code = code;
    this.messages = messages;
  }

  public int getCode() {
    return code;
  }

  public T getData() {
    return data;
  }

  public List<String> getMessages() {
    return messages;
  }
}
