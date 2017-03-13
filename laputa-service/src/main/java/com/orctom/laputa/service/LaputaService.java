package com.orctom.laputa.service;

import com.orctom.laputa.exception.IllegalArgException;
import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.config.MappingConfig;
import com.orctom.laputa.service.internal.Bootstrapper;
import com.orctom.laputa.service.controller.DefaultController;
import com.orctom.laputa.service.lifecycle.PostStart;
import com.orctom.laputa.service.lifecycle.PreStart;
import com.orctom.laputa.service.translator.ResponseTranslator;
import com.orctom.laputa.service.translator.ResponseTranslators;
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
import java.util.ServiceLoader;

import static com.orctom.laputa.service.Constants.*;

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
    printAsciiArt();
    validate(configurationClass);
    preStart();
    createApplicationContext(configurationClass);
    loadResponseTranslators();
    startup();
    postStart();
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
    registerBean(DefaultController.class, "defaultHandler");
  }

  private void loadResponseTranslators() {
    ServiceLoader.load(ResponseTranslator.class).forEach(ResponseTranslators::register);
  }

  private void startup() {
    Config config = Configurator.getInstance().getConfig();
    loadMappings();

    LOGGER.info("Starting service...");
    boolean bootstrapHttpsService = config.hasPath(CFG_SERVER_HTTPS_PORT);
    if (bootstrapHttpsService) {
      bootstrapHttpsService(config.getInt(CFG_SERVER_HTTPS_PORT));
    }

    boolean bootstrapHttpService = config.hasPath(CFG_SERVER_HTTP_PORT);
    if (bootstrapHttpService) {
      bootstrapHttpService(config.getInt(CFG_SERVER_HTTP_PORT));
    }

    if (!bootstrapHttpService && !bootstrapHttpsService) {
      bootstrapHttpService(DEFAULT_HTTP_PORT);
    }
  }

  private void loadMappings() {
    MappingConfig.getInstance().scan(applicationContext);
  }

  private void bootstrapHttpsService(int port) {
    new Bootstrapper(port, true).start();
  }

  private void bootstrapHttpService(int port) {
    new Bootstrapper(port, false).start();
  }

  private void printAsciiArt() {
    try (InputStream in = getClass().getResourceAsStream("/laputa");
         BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
      reader.lines().forEach(System.out::println);
    } catch (Exception ignored) {
    }
  }

  private void preStart() {
    LOGGER.info("Pre-start:");
    ServiceLoader.load(PreStart.class).forEach(preStart -> {
      LOGGER.info("\t{}", preStart.getClass());
      preStart.run();
    });
  }

  private void postStart() {
    LOGGER.info("Post-start:");
    ServiceLoader.load(PostStart.class).forEach(postStart -> {
      LOGGER.info("\t{}", postStart.getClass());
      postStart.run();
    });
  }
}
