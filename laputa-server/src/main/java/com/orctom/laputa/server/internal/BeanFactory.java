package com.orctom.laputa.server.internal;

public interface BeanFactory {

	public <T> T getInstance(Class<T> clazz);

	public static BeanFactory getFactory() {
		return new NaiveBeanFactory();
	}
}
