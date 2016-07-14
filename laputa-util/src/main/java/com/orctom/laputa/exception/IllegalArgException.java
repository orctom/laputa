package com.orctom.laputa.exception;

public class IllegalArgException extends FastException {

  public IllegalArgException(Throwable cause) {
    super(cause);
  }

  public IllegalArgException(String message) {
    super(message);
  }

  public IllegalArgException(String message, Throwable cause) {
    super(message, cause);
  }
}
