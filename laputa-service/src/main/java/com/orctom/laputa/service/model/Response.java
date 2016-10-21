package com.orctom.laputa.service.model;

import java.util.List;

public class Response {

  private int code = 200;
  private List<String> messages;

  public Response() {
  }

  public Response(int code, List<String> messages) {
    this.code = code;
    this.messages = messages;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public List<String> getMessages() {
    return messages;
  }

  public void setMessages(List<String> messages) {
    this.messages = messages;
  }

  @Override
  public String toString() {
    return "Response{" +
        "code=" + code +
        ", messages=" + messages +
        '}';
  }
}
