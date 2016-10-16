package com.orctom.laputa.exception;

/**
 * Base exception class that do not fillInStackTrace
 * Created by hao on 7/11/16.
 */
public abstract class FastException extends RuntimeException {

  public FastException() {
  }

  public FastException(Throwable cause) {
    super(cause);
  }

  public FastException(String message) {
    super(message);
  }

  public FastException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public Throwable fillInStackTrace() {
    return null;
  }
}
