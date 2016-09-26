package com.orctom.laputa.server.config;

import com.orctom.laputa.server.internal.BeanFactory;
import com.orctom.laputa.server.internal.NaiveBeanFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Config items
 * Created by hao on 1/6/16.
 */
public class ServiceConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfig.class);

  private static final ServiceConfig INSTANCE = new ServiceConfig();
  private Boolean debugEnabled;
  private Charset charset;
  private Config config;
  private BeanFactory beanFactory = new NaiveBeanFactory();

  private ServiceConfig() {
    initConfig();
    initDebugFlag();
    initCharset();
  }

  public static Path getAppRootDir() {
    return Paths.get("").toAbsolutePath();
  }

  public static ServiceConfig getInstance() {
    return INSTANCE;
  }

  private void initConfig() {
    final Config reference = ConfigFactory.load("reference");
    config = ConfigFactory.load().withFallback(reference);
  }

  private void initDebugFlag() {
    try {
      debugEnabled = config.getBoolean("debug.enabled");
    } catch (Exception e) {
      debugEnabled = false;
    }
  }

  private void initCharset() {
    try {
      String charsetName = ServiceConfig.getInstance().getConfig().getString("charset");
      charset = Charset.forName(charsetName);
    } catch (ConfigException e) {
      LOGGER.info("`charset` is not configured, using system default.");
    } catch (UnsupportedCharsetException e) {
      LOGGER.error("Unsupported charset: {}, using system default.", e.getCharsetName());
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

  public Charset getCharset() {
    return charset;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }
}
