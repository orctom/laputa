package com.orctom.laputa.server.model;

import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestMapping {

  private String uriPattern;
  private Object target;
  private FastMethod handlerMethod;
  private String httpMethod;

  public RequestMapping(String uriPattern,
                        Object target,
                        Class<?> handlerClass,
                        Method handlerMethod,
                        String httpMethod) {
    this.uriPattern = uriPattern;
    this.target = target;
    this.handlerMethod = FastClass.create(handlerClass).getMethod(handlerMethod);
    this.httpMethod = httpMethod;
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

  @Override
  public String toString() {
    return uriPattern + " -> " + handlerMethod.getJavaMethod().toGenericString() + " " + httpMethod;
  }
}
