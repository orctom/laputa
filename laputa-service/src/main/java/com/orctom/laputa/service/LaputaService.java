package com.orctom.laputa.service;

import com.orctom.laputa.exception.IllegalArgException;
import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.config.MappingConfig;
import com.orctom.laputa.service.internal.Bootstrapper;
import com.orctom.laputa.service.internal.handler.DefaultHandler;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Serving http
 * Created by hao on 9/10/15.
 */
public class LaputaService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaputaService.class);

  private static LaputaService INSTANCE = new LaputaService();

  private AnnotationConfigApplicationContext applicationContext;

  private LaputaService() {
  }

  public static LaputaService getInstance() {
    return INSTANCE;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public void registerBean(Class<?> beanClass, String name) {
    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
    beanDefinition.setBeanClass(beanClass);
    beanDefinition.setLazyInit(false);
    beanDefinition.setAbstract(false);
    applicationContext.registerBeanDefinition(name, beanDefinition);
  }

  public void run(Class<?> configurationClass) {
    validate(configurationClass);
    createApplicationContext(configurationClass);
    startup();
  }

  private void validate(Class<?> configurationClass) {
    if (null == configurationClass) {
      throw new IllegalArgException("Null class to 'run()'!");
    }
    if (!configurationClass.isAnnotationPresent(Configuration.class)) {
      throw new IllegalArgException("@Configuration is expected on class: " + configurationClass);
    }
  }

  private void createApplicationContext(Class<?> configurationClass) {
    applicationContext = new AnnotationConfigApplicationContext(configurationClass);
    registerBean(DefaultHandler.class, "defaultHandler");
  }

  private void startup() {
    printAsciiArt();
    loadMappings();
    LOGGER.info("Starting service...");
    bootstrapHttpsService();
    bootstrapHttpService();
  }

  private void loadMappings() {
    MappingConfig.getInstance().scan(applicationContext);
  }

  private void bootstrapHttpsService() {
    Config config = Configurator.getInstance().getConfig();
    try {
      int port = config.getInt("server.https.port");
      new Bootstrapper(port, true).start(); // start https in separate thread
    } catch (Exception e) {
      LOGGER.warn("Skipped to start https service, due to: {}", e.getMessage());
    }
  }

  private void bootstrapHttpService() {
    Config config = Configurator.getInstance().getConfig();
    try {
      int port = config.getInt("server.http.port");
      new Bootstrapper(port, false).run(); // not start http in separate thread
    } catch (Exception e) {
      LOGGER.warn("Skipped to start http service, due to: {}", e.getMessage());
    }
  }

  private void printAsciiArt() {
    try (InputStream in = getClass().getResourceAsStream("/laputa");
         BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
      reader.lines().forEach(System.out::println);
    } catch (Exception ignored) {
    }
  }
}
