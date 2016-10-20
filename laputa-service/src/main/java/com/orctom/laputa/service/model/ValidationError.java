package com.orctom.laputa.service.model;

import java.util.List;

/**
 * Validation error info
 * Created by chenhao on 10/9/16.
 */
public class ValidationError extends Response {

  public ValidationError(List messages) {
    super(messages);
  }
}
