package com.orctom.laputa.consul.exception;

public class ConsulException extends RuntimeException {

  public ConsulException(Exception e) {
    super(e);
  }

  public ConsulException(String msg, Exception e) {
    super(msg, e);
  }
}
