package com.orctom.laputa;

import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.Reflection;
import com.orctom.laputa.internal.ServiceInterceptor;

import com.orctom.laputa.annotation.LaputaService;
import com.orctom.laputa.util.AnnotationUtils;
import com.orctom.laputa.util.ServiceRegistryUtils;

/**
 * Client
 * Created by hao on 4/28/15.
 */
public class Laputa {

	private static Map<String, String> SERVICE_URL_MAPPING = new HashMap<>();

	public static <T> T instrument(Class<T> interfaceClass) {
		return Reflection.newProxy(interfaceClass, new ServiceInterceptor());
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
