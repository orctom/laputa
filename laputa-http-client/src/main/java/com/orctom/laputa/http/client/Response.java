package com.orctom.laputa.http.client;

import com.google.common.primitives.Bytes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Response {

  private Map<String, ?> headers = new HashMap<>();
  private byte[] content;

  public Map<String, ?> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, ?> headers) {
    this.headers = headers;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public void appendContent(byte[] appends) {
    if (null == content) {
      content = appends;
    } else {
      content = new byte[content.length + appends.length];
      content = Bytes.concat(content, appends);
    }
  }
}
