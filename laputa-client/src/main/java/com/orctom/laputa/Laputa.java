package com.orctom.laputa;

import java.util.HashMap;
import java.util.Map;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import com.orctom.laputa.annotation.LaputaService;
import com.orctom.laputa.util.AnnotationUtils;
import com.orctom.laputa.util.ServiceRegistryUtils;

/**
 * Created by hao on 4/28/15.
 */
public class Laputa {

	private static Map<String, String> SERVICE_URL_MAPPING = new HashMap<>();

	public static <T> T instrument(Class<T> interfaceClass) {
		Class<? extends T> dynamicType = new ByteBuddy()
				.subclass(interfaceClass)
				.method(ElementMatchers.isMethod())
				.intercept(MethodDelegation.to(new ServiceInterceptor()))
				.make()
				.load(getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
				.getLoaded();
		try {
			return dynamicType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	private static ClassLoader getClassLoader() {
		ClassLoader cl = Thread.currentThread().getClass().getClassLoader();
		if (null == cl) {
			cl = Laputa.class.getClassLoader();
		}
		return cl;
	}

	private static String getServiceId(Class<?> clazz) {
		LaputaService ls = AnnotationUtils.findAnnotation(clazz, LaputaService.class);
		if (null == ls) {
			throw new UnsupportedOperationException("LaputaService annotation expected on target interface");
		}
		return ls.value();
	}

	private static boolean isServiceRegistered(String serviceId) {
		String serviceURL = ServiceRegistryUtils.lookup(serviceId);
		if (null != serviceURL) {
			SERVICE_URL_MAPPING.put(serviceId, serviceURL);
			return true;
		}
		return false;
	}
}
