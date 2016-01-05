package com.orctom.laputa.server.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * FIXME
 * Utils to set/get properties or invoke methods dynamically
 * Created by hao on 1/5/16.
 */
public abstract class ArgsResolver {

	public static Object[] resolveArgs(Method method, Map<String, String> paramValues) {
		Parameter[] methodParameters = method.getParameters();
		if (0 == methodParameters.length) {
			return null;
		}

		Object[] args = new Object[methodParameters.length];

		Map<Parameter, Integer> unresoved = new HashMap<>();
		for (int i = 0; i < methodParameters.length; i++) {
			Parameter parameter = methodParameters[i];
			String paramName = parameter.getName();
			String paramValue = paramValues.get(paramName);
			if (null !=  paramValue) {
				args[i] = paramValue;
			} else {
				unresoved.put(parameter, i);
			}
		}
		return null;
	}
}
