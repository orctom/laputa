package com.orctom.laputa.server.model;

import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestMapping {

  private String uriPattern;
  private Class<?> handlerClass;
  private FastMethod handlerMethod;

  public RequestMapping(String uriPattern, Class<?> handlerClass, Method handlerMethod) {
    this.uriPattern = uriPattern;
    this.handlerClass = handlerClass;
    this.handlerMethod = FastClass.create(handlerClass).getMethod(handlerMethod);
  }

  public String getUriPattern() {
    return uriPattern;
  }

  public Class<?> getHandlerClass() {
    return handlerClass;
  }

  public Parameter[] getParameters() {
    return handlerMethod.getJavaMethod().getParameters();
  }

  public FastMethod getHandlerMethod() {
    return handlerMethod;
  }

  @Override
  public String toString() {
    return uriPattern + " -> " + handlerClass.getName() + " " + handlerMethod.getName();
  }
}
