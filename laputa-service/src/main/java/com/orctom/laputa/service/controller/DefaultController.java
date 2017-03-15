package com.orctom.laputa.service.controller;

import com.orctom.laputa.service.annotation.Path;

import static com.orctom.laputa.service.Constants.*;

/**
 * Default controller
 * Created by hao on 11/17/15.
 */
public class DefaultController {

  @Path(PATH_FAVICON)
  public String _favicon() {
    return null;
  }

  @Path(PATH_404)
  public String _404() {
    return "The requested resource does not exist.";
  }

  @Path(PATH_500)
  public String _500() {
    return "The server can not process your last request, please try again later.";
  }

  @Path(PATH_ERROR)
  public String error() {
    return "Please verify your request and try again later.";
  }
}
