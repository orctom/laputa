package com.orctom.laputa.server.config;

import com.orctom.laputa.server.internal.BeanFactory;
import com.orctom.laputa.server.internal.NaiveBeanFactory;

/**
 * Config items
 * Created by hao on 1/6/16.
 */
public class ServiceConfig {

	private BeanFactory beanFactory = new NaiveBeanFactory();

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	private static final ServiceConfig INSTANCE = new ServiceConfig();

	private ServiceConfig() {}

	public static ServiceConfig getInstance() {
		return INSTANCE;
	}
}
