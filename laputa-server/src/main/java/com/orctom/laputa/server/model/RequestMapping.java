package com.orctom.laputa.server.model;

import java.lang.reflect.Method;

public class RequestMapping {

  private String uriPattern;
  private Class<?> handlerClass;
  private Method handlerMethod;

  public RequestMapping(String uriPattern, Class<?> handlerClass, Method handlerMethod) {
    this.uriPattern = uriPattern;
    this.handlerClass = handlerClass;
    this.handlerMethod = handlerMethod;
  }

  public String getUriPattern() {
    return uriPattern;
  }

  public Class<?> getHandlerClass() {
    return handlerClass;
  }

  public Method getHandlerMethod() {
    return handlerMethod;
  }

  @Override
  public String toString() {
    return uriPattern + " -> " + handlerClass.getName() + " " + handlerMethod.getName();
  }
}
