package com.orctom.laputa.server.exception;

import com.orctom.exception.FastException;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Param validation exception
 * Created by chenhao on 10/9/16.
 */
public class ParameterValidationException extends FastException {

  private List<String> messages = new ArrayList<>();

  public ParameterValidationException(String message) {
    messages.add(message);
  }

  public ParameterValidationException(Set<ConstraintViolation<Object>> violations) {
    for (ConstraintViolation<Object> violation : violations) {
      System.out.println(violation.getRootBean());
      messages.add("Invalid param value: " + violation.getInvalidValue() + ", " + violation.getMessage());
    }
  }

  @Override
  public String getMessage() {
    StringBuilder msg = new StringBuilder();
    for (String message : messages) {
      msg.append(message).append(", ");
    }
    return msg.deleteCharAt(msg.length() - 2).toString();
  }

  public List<String> getMessages() {
    return messages;
  }
}
