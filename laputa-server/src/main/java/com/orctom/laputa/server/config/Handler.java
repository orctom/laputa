package com.orctom.laputa.server.config;

import java.lang.reflect.Method;

public class Handler {

	private Class<?> handlerClass;
	private Method handlerMethod;

	public Handler(Class<?> handlerClass, Method handlerMethod) {
		this.handlerClass = handlerClass;
		this.handlerMethod = handlerMethod;
	}

	public Class<?> getHandlerClass() {
		return handlerClass;
	}

	public Method getHandlerMethod() {
		return handlerMethod;
	}
}
