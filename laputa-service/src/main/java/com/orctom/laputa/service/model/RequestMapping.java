package com.orctom.laputa.service.model;

import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestMapping {

  private String uriPattern;
  private Object target;
  private FastMethod handlerMethod;
  private String httpMethod;
  private String redirectTo;

  public RequestMapping(String uriPattern,
                        Object target,
                        Class<?> handlerClass,
                        Method handlerMethod,
                        String httpMethod,
                        String redirectTo) {
    this.uriPattern = uriPattern;
    this.target = target;
    this.handlerMethod = FastClass.create(handlerClass).getMethod(handlerMethod);
    this.httpMethod = httpMethod;
    this.redirectTo = redirectTo;
  }

  public String getUriPattern() {
    return uriPattern;
  }

  public Object getTarget() {
    return target;
  }

  public Parameter[] getParameters() {
    return handlerMethod.getJavaMethod().getParameters();
  }

  public FastMethod getHandlerMethod() {
    return handlerMethod;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public String getRedirectTo() {
    return redirectTo;
  }

  @Override
  public String toString() {
    return uriPattern + " " + httpMethod + " -> " + handlerMethod.getJavaMethod().toGenericString();
  }
}
