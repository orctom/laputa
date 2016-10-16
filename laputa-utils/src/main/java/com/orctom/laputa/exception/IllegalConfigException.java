package com.orctom.laputa.exception;

public class IllegalConfigException extends FastException {

  public IllegalConfigException(Throwable cause) {
    super(cause);
  }

  public IllegalConfigException(String message) {
    super(message);
  }

  public IllegalConfigException(String message, Throwable cause) {
    super(message, cause);
  }
}
