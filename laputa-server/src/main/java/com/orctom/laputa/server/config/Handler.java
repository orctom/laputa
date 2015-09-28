package com.orctom.laputa.server.config;

import io.netty.handler.codec.http.HttpResponse;

import java.lang.reflect.Method;

public class Handler {

	private String path;
	private Class<?> handlerClass;
	private Method handlerMethod;

	public Handler(String path, Class<?> handlerClass, Method handlerMethod) {
		this.path = path;
		this.handlerClass = handlerClass;
		this.handlerMethod = handlerMethod;
	}

	public Class<?> getHandlerClass() {
		return handlerClass;
	}

	public Method getHandlerMethod() {
		return handlerMethod;
	}

	public Object process(String uri) {
		return null;
	}

	@Override
	public String toString() {
		return path + " -> " + handlerClass.getName() + " " + handlerMethod.getName();
	}
}
