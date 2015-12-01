package com.orctom.laputa.server.config;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.orctom.laputa.server.PathParamsUtils;
import com.orctom.laputa.server.internal.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Handler {

	private String uriPattern;
	private Class<?> handlerClass;
	private Method handlerMethod;

	public Handler(String uriPattern, Class<?> handlerClass, Method handlerMethod) {
		this.uriPattern = uriPattern;
		this.handlerClass = handlerClass;
		this.handlerMethod = handlerMethod;
	}

	public Class<?> getHandlerClass() {
		return handlerClass;
	}

	public Method getHandlerMethod() {
		return handlerMethod;
	}

	public Object process(String uri, String queryString) throws InvocationTargetException, IllegalAccessException {
		Object target = BeanFactory.getFactory().getInstance(handlerClass);
		Optional<Map<String, String>> params = PathParamsUtils.extractParams(uriPattern, uri, queryString);

		if (params.isPresent()) {
			Object args = resolveArgs(params.get());
			return handlerMethod.invoke(target, args);
		} else {
			return handlerMethod.invoke(target);
		}
	}

	/**
	 * TODO
	 * 1. type casting
	 * 2. complex object mapping
	 */
	private Object[] resolveArgs(Map<String, String> params) {
		for (Map.Entry<String, String> entry : params.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}

		Parameter[] parameters = handlerMethod.getParameters();
		Object[] args = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter param = parameters[i];
			Class<?> type = param.getType();
			if (String.class.equals(type)) {
				args[i] = params.get(param.getName());
			}
		}

		for(Parameter param : handlerMethod.getParameters()) {
			Class<?> type = param.getType();
			if (String.class.equals(type)) {

			}
			System.out.println(param.getName());
			System.out.println(param.getParameterizedType());
			System.out.println(param.getType());
			System.out.println("====");
			String arg = params.get(param.getName());
		}
		return args;
	}

	@Override
	public String toString() {
		return uriPattern + " -> " + handlerClass.getName() + " " + handlerMethod.getName();
	}
}
