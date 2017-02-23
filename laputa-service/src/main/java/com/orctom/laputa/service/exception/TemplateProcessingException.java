package com.orctom.laputa.service.exception;

import com.orctom.laputa.exception.FastException;

public class TemplateProcessingException extends FastException {

  public TemplateProcessingException(String message) {
    super(message);
  }

  public TemplateProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
