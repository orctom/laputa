package com.orctom.laputa.server;

import com.google.common.base.Preconditions;
import com.orctom.laputa.exception.ClassLoadingException;
import com.orctom.laputa.server.config.MappingConfig;
import com.orctom.laputa.server.config.ServiceConfig;
import com.orctom.laputa.server.internal.BeanFactory;
import com.orctom.laputa.server.internal.Bootstrapper;

import java.lang.annotation.Annotation;

/**
 * Serving http
 * Created by hao on 9/10/15.
 */
public class LaputaService {

  private static LaputaService INSTANCE = new LaputaService();

  private String[] basePackages;
  private Class<? extends Annotation> annotation;

  private Bootstrapper bootstrapper;

  private LaputaService() {
    bootstrapper = new Bootstrapper();
  }

  public static LaputaService getInstance() {
    return INSTANCE;
  }

  public LaputaService scanPackage(String... basePackages) {
    this.basePackages = basePackages;
    return this;
  }

  public LaputaService forAnnotation(Class<? extends Annotation> annotation) {
    this.annotation = annotation;
    return this;
  }

  public LaputaService enableDebug(boolean setDebugEnabled) {
    ServiceConfig.getInstance().setDebugEnabled(setDebugEnabled);
    return this;
  }

  public LaputaService withBeanFactory(BeanFactory beanFactory) {
    ServiceConfig.getInstance().setBeanFactory(beanFactory);
    return this;
  }

  public void startup() throws Exception {
    validate();
    loadMappings();
    bootstrapper.bootstrapService();
  }

  private void validate() {
    Preconditions.checkArgument(null != basePackages, "'base packages' not set");
    Preconditions.checkArgument(null != annotation, "'annotation' to scan not set");
  }

  private void loadMappings() throws ClassLoadingException {
    MappingConfig.getInstance().scan(annotation, basePackages);
  }

}
