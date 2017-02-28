package com.orctom.laputa.service.config;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.orctom.laputa.utils.HostUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
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

  private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd,yyyyMMdd,yyyy-MM-dd HH:mm:ss,dd/mm/yyyy";
  private static final String DEFAULT_CHARSET = "UTF-8";

  private static final String DIR_HOST = "host/";

  private Config config;
  private boolean debugEnabled;
  private Charset charset;
  private String staticFilesDir;
  private Integer requestRateLimit;
  private long httpDataUseDiskMinSize = DefaultHttpDataFactory.MINSIZE;

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
    String appRootDir = Paths.get(SIGN_DOT).toAbsolutePath().toString();

    config = ConfigFactory.parseString("appRootDir=\"" + appRootDir + "\"");
    String hostname = HostUtils.getHostname();
    if (!Strings.isNullOrEmpty(hostname)) {
      config = config.withFallback(ConfigFactory.load(DIR_HOST + hostname));
    }
    config = config.withFallback(ConfigFactory.load());
  }

  private void initDebugFlag() {
    if (config.hasPath(CFG_DEBUG)) {
      debugEnabled = config.getBoolean(CFG_DEBUG);
    }
  }

  private void initDatePattern() {
    String pattern = DEFAULT_DATE_PATTERN;
    if (config.hasPath(CFG_DATE_PATTERN)) {
      pattern = config.getString(CFG_DATE_PATTERN);
    } else {
      LOGGER.warn("`{}` is not configured, using default: {}", CFG_DATE_PATTERN, DEFAULT_DATE_PATTERN);
    }

    List<String> splits = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(pattern);
    String[] patterns = splits.toArray(new String[splits.size()]);

    DateConverter converter = new DateConverter();
    converter.setPatterns(patterns);
    ConvertUtils.register(converter, Date.class);
  }

  private void initCharset() {
    charset = Charset.forName(DEFAULT_CHARSET);
    if (config.hasPath(CFG_CHARSET)) {
      String charsetName = config.getString(CFG_CHARSET);
      try {
        charset = Charset.forName(charsetName);
      } catch (UnsupportedCharsetException e) {
        LOGGER.error("Unsupported charset: {}, using default: {}.", charsetName, DEFAULT_CHARSET);
      }
    } else {
      LOGGER.warn("`{}` is not configured, using default: {}.", CFG_CHARSET, DEFAULT_CHARSET);
    }
  }

  private void initStaticFilesDir() {
    if (config.hasPath(CFG_STATIC_DIR)) {
      staticFilesDir = config.getString(CFG_STATIC_DIR);
    } else {
      LOGGER.warn("`{}` is not configured, using system temporal.", CFG_STATIC_DIR);
    }
  }

  private void initRateLimiter() {
    if (config.hasPath(CFG_THROTTLE)) {
      requestRateLimit = config.getInt(CFG_THROTTLE);
    } else {
      LOGGER.info("`{}` is not configured, request rate limiter is turned off.", CFG_THROTTLE);
    }
  }

  private void initHttpDataUseDiskMinSize() {
    if (config.hasPath(CFG_HTTPDATA_USEDISK_MINSIZE)) {
      httpDataUseDiskMinSize = config.getLong(CFG_HTTPDATA_USEDISK_MINSIZE);
      LOGGER.info("set `{}` to {} bytes.", CFG_HTTPDATA_USEDISK_MINSIZE, httpDataUseDiskMinSize);
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

  public long getHttpDataUseDiskMinSize() {
    return httpDataUseDiskMinSize;
  }
}
