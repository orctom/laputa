package com.orctom.laputa.server.config;

import com.orctom.laputa.server.internal.BeanFactory;
import com.orctom.laputa.server.internal.NaiveBeanFactory;

/**
 * Config items
 * Created by hao on 1/6/16.
 */
public class ServiceConfig {

  private boolean debugEnabled = false;
  private BeanFactory beanFactory = new NaiveBeanFactory();

  private static final ServiceConfig INSTANCE = new ServiceConfig();

  private ServiceConfig() {
  }

  public static ServiceConfig getInstance() {
    return INSTANCE;
  }

  public BeanFactory getBeanFactory() {
    return beanFactory;
  }

  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  public boolean isDebugEnabled() {
    return debugEnabled;
  }

  public void setDebugEnabled(boolean debugEnabled) {
    this.debugEnabled = debugEnabled;
  }
}
