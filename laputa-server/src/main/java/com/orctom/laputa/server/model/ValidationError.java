package com.orctom.laputa.server.model;

import java.util.List;

/**
 * Validation error info
 * Created by chenhao on 10/9/16.
 */
public class ValidationError {

  private List<String> messages;

  public ValidationError(List<String> messages) {
    this.messages = messages;
  }

  public List<String> getMessages() {
    return messages;
  }
}
