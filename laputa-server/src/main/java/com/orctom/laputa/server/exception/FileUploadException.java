package com.orctom.laputa.server.exception;

import com.orctom.exception.FastException;

public class FileUploadException extends FastException {

  public FileUploadException(String message) {
    super(message);
  }

  public FileUploadException(String message, Throwable cause) {
    super(message, cause);
  }
}
