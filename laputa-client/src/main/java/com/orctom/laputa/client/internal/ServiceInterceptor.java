package com.orctom.laputa.client.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * InvocationHandler for proxy
 * Created by hao on 6/15/15.
 */
public class ServiceInterceptor implements InvocationHandler {

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    System.out.println("InvocationHandler " + method.toString());
    System.out.println(Arrays.toString(args));
    return null;
  }
}
