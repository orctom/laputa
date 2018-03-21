package com.orctom.laputa.service.controller;

import com.orctom.laputa.service.annotation.PATH;

import static com.orctom.laputa.service.Constants.*;

/**
 * Default controller
 * Created by hao on 11/17/15.
 */
public class DefaultController {

  @PATH(PATH_FAVICON)
  public String _favicon() {
    return null;
  }

  @PATH(PATH_404)
  public String _404() {
    return "The requested resource does not exist.";
  }

  @PATH(PATH_500)
  public String _500() {
    return "The server can not process your last request, please try again later.";
  }

  @PATH(PATH_ERROR)
  public String error() {
    return "Please verify your request and try again later.";
  }
}
