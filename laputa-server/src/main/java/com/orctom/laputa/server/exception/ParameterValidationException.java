package com.orctom.laputa.server.exception;

import com.orctom.exception.FastException;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * Param validation exception
 * Created by chenhao on 10/9/16.
 */
public class ParameterValidationException extends FastException {

  private String message;

  public ParameterValidationException(Set<ConstraintViolation<Object>> violations) {
    StringBuilder msg = new StringBuilder();
    for (ConstraintViolation<Object> violation : violations) {
      msg.append(violation.getMessage()).append(", ");
    }

    this.message = msg.deleteCharAt(msg.length() - 2).toString();
  }

  @Override
  public String getMessage() {
    return message;
  }
}
