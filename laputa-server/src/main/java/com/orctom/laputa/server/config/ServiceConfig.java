package com.orctom.laputa.server.config;

import com.orctom.laputa.server.internal.BeanFactory;
import com.orctom.laputa.server.internal.NaiveBeanFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Config items
 * Created by hao on 1/6/16.
 */
public class ServiceConfig {

  private static final ServiceConfig INSTANCE = new ServiceConfig();
  private Boolean debugEnabled;
  private Config config;
  private BeanFactory beanFactory = new NaiveBeanFactory();

  private ServiceConfig() {
    initConfig();
    initDebugFlag();
  }

  public static Path getAppRootDir() {
    return Paths.get("").toAbsolutePath();
  }

  public static ServiceConfig getInstance() {
    return INSTANCE;
  }

  private void initConfig() {
    config = ConfigFactory.load();
  }

  private void initDebugFlag() {
    try {
      debugEnabled = config.getBoolean("debug.enabled");
    } catch (Exception e) {
      debugEnabled = false;
    }
  }


  public Config getConfig() {
    return config;
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
}
