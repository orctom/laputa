package com.orctom.laputa.service.config;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.orctom.laputa.service.model.SecurityConfig;
import com.orctom.laputa.utils.HostUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.orctom.laputa.service.Constants.*;

/**
 * Config items
 * Created by hao on 1/6/16.
 */
public class Configurator {

  private static final Logger LOGGER = LoggerFactory.getLogger(Configurator.class);

  private static final Configurator INSTANCE = new Configurator();

  private static final String DIR_HOST = "host/";

  private Config config;
  private boolean debugEnabled;
  private Charset charset;
  private Integer throttle;
  private long postDataUseDiskThreshold = DefaultHttpDataFactory.MINSIZE;
  private SecurityConfig securityConfig;

  private Configurator() {
    initConfig();
    loadDebugFlag();
    loadDatePattern();
    loadCharset();
    loadPostDataUseDiskThreshold();
    loadThrottle();
    loadSecurityConfig();
  }

  public static Configurator getInstance() {
    return INSTANCE;
  }

  private void initConfig() {
    String appRootDir = Paths.get(SIGN_DOT).toAbsolutePath().toString();
    LOGGER.info("Set `{}` to: {}", CFG_APP_ROOT, appRootDir);

    System.setProperty(CFG_APP_ROOT, appRootDir);

    config = ConfigFactory.parseString(CFG_APP_ROOT + "=\"" + appRootDir + "\"");
    String hostname = HostUtils.getHostname();
    if (isHostConfigExist(hostname)) {
      config = config.withFallback(ConfigFactory.load(DIR_HOST + hostname));
    }
    config = config.withFallback(ConfigFactory.load());
  }

  private boolean isHostConfigExist(String hostname) {
    if (Strings.isNullOrEmpty(hostname)) {
      return false;
    }
    return null != getClass().getResource(File.pathSeparator + hostname + ".conf");
  }

  private void loadDebugFlag() {
    debugEnabled = config.getBoolean(CFG_DEBUG);
  }

  private void loadDatePattern() {
    String pattern = config.getString(CFG_DATE_PATTERN);
    LOGGER.info("Setting date format to: {}", pattern);

    List<String> splits = Splitter.on(SIGN_SEMI_COLON).omitEmptyStrings().trimResults().splitToList(pattern);
    String[] patterns = splits.toArray(new String[splits.size()]);

    DateConverter converter = new DateConverter();
    converter.setPatterns(patterns);
    ConvertUtils.register(converter, Date.class);
  }

  private void loadCharset() {
    String charsetName = config.getString(CFG_CHARSET);
    charset = Charset.forName(charsetName);
    LOGGER.info("Setting charset to: {}", charsetName);
  }

  private void loadPostDataUseDiskThreshold() {
    postDataUseDiskThreshold = config.getLong(CFG_POSTDATA_USEDISK_THRESHOLD);
    LOGGER.info("Setting `{}` to {} bytes.", CFG_POSTDATA_USEDISK_THRESHOLD, postDataUseDiskThreshold);
  }

  private void loadThrottle() {
    if (config.hasPath(CFG_THROTTLE)) {
      throttle = config.getInt(CFG_THROTTLE);
    } else {
      LOGGER.info("Setting request rate limiter off, `{}` is not configured.", CFG_THROTTLE);
    }
  }

  private void loadSecurityConfig() {
    if (!config.hasPath(CFG_SECURITY_RESOURCES)) {
      return;
    }

    List<String> resources = config.getStringList(CFG_SECURITY_RESOURCES);
    if (config.hasPath(CFG_SECURITY_NON_RESOURCES)) {
      List<String> nonResources = config.getStringList(CFG_SECURITY_NON_RESOURCES);
      securityConfig = new SecurityConfig(resources, nonResources);
    } else {
      securityConfig = new SecurityConfig(resources);
    }
  }

  public Config getConfig() {
    return config;
  }

  public boolean isDebugEnabled() {
    return debugEnabled;
  }

  public Charset getCharset() {
    return charset;
  }

  public Integer getThrottle() {
    return throttle;
  }

  public long getPostDataUseDiskThreshold() {
    return postDataUseDiskThreshold;
  }

  public SecurityConfig getSecurityConfig() {
    return securityConfig;
  }
}
