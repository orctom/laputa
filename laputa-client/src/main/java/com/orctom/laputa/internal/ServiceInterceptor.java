package com.orctom.laputa.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * InvocationHandler for proxy
 * Created by hao on 6/15/15.
 */
public class ServiceInterceptor implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("InvocationHandler " + method.toString());
		return null;
	}
}
