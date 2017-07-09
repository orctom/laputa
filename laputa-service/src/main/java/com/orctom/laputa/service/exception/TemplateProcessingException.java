package com.orctom.laputa.service.exception;

public class TemplateProcessingException extends RuntimeException {

  public TemplateProcessingException(String message) {
    super(message);
  }

  public TemplateProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
