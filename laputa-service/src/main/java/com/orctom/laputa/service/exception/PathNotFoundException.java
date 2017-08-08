package com.orctom.laputa.service.exception;

import com.orctom.laputa.exception.FastException;

public class PathNotFoundException extends FastException {

  public PathNotFoundException(Throwable cause) {
    super(cause);
  }

  public PathNotFoundException(String message) {
    super(message);
  }

  public PathNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
