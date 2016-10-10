package com.orctom.laputa.server.config;

import com.google.common.base.Splitter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

/**
 * Config items
 * Created by hao on 1/6/16.
 */
public class Configurator {

  private static final Logger LOGGER = LoggerFactory.getLogger(Configurator.class);

  private static final Configurator INSTANCE = new Configurator();

  private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd,yyyyMMdd,yyyy-MM-dd HH:mm:ss";

  private Config config;
  private boolean debugEnabled;
  private Charset charset;
  private String staticFilesDir;
  private Integer requestRateLimit;

  private Configurator() {
    initConfig();
    initDebugFlag();
    initDatePattern();
    initCharset();
    initStaticFilesDir();
    initRateLimiter();
  }

  public static Configurator getInstance() {
    return INSTANCE;
  }

  private void initConfig() {
    String appRootDir = Paths.get(".").toAbsolutePath().toString();

    config = ConfigFactory.parseString("appRootDir=\"" + appRootDir + "\"")
        .withFallback(ConfigFactory.load())
        .withFallback(ConfigFactory.load("reference"));
  }

  private void initDebugFlag() {
    try {
      debugEnabled = config.getBoolean("debug.enabled");
    } catch (Exception e) {
      debugEnabled = false;
    }
  }

  private void initDatePattern() {
    String pattern;
    try {
      pattern = config.getString("date.pattern");
    } catch (Exception e) {
      LOGGER.warn("`date.pattern` is not configured, using default: {}", DEFAULT_DATE_PATTERN);
      pattern = DEFAULT_DATE_PATTERN;
    }

    List<String> splits = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(pattern);
    String[] patterns = splits.toArray(new String[splits.size()]);

    DateConverter converter = new DateConverter();
    converter.setPatterns(patterns);
    ConvertUtils.register(converter, Date.class);
  }

  private void initCharset() {
    try {
      String charsetName = config.getString("charset");
      charset = Charset.forName(charsetName);
    } catch (ConfigException e) {
      LOGGER.warn("`charset` is not configured, using system default.");
    } catch (UnsupportedCharsetException e) {
      LOGGER.error("Unsupported charset: {}, using system default.", e.getCharsetName());
    }
  }

  private void initStaticFilesDir() {
    try {
      staticFilesDir = config.getString("static.files.dir");
    } catch (ConfigException e) {
      LOGGER.warn("`static.files.dir` is not configured, using system temporal.");
    }
  }

  private void initRateLimiter() {
    try {
      requestRateLimit = config.getInt("server.requests.rate.limit");
    } catch (ConfigException e) {
      LOGGER.info("`server.requests.rate.limit` is not configured, request rate limiter is turned off.");
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

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  public String getStaticFilesDir() {
    return staticFilesDir;
  }

  public Integer getRequestRateLimit() {
    return requestRateLimit;
  }
}
