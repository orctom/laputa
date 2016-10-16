package com.orctom.laputa.server.exception;

import com.orctom.laputa.exception.FastException;

public class RequestProcessingException extends FastException {

  public RequestProcessingException(String message) {
    super(message);
  }

  public RequestProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
