package com.orctom.laputa.service;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Constants {

  public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss ZZ";
  public static final DateTimeFormatter HTTP_DATE_FORMATTER = DateTimeFormat.forPattern(HTTP_DATE_FORMAT);

  public static final String CFG_APP_ROOT = "app.root";
  public static final String CFG_DEBUG = "debug";
  public static final String CFG_DATE_PATTERN = "date.pattern";
  public static final String CFG_CHARSET = "charset";
  public static final String CFG_URLS_STATIC_DEFAULT_MAPPINGS = "urls.static.default.mappings";
  public static final String CFG_URLS_STATIC_MAPPINGS = "urls.static.mappings";
  public static final String CFG_URI = "uri";
  public static final String CFG_PATH = "path";
  public static final String CFG_UPLOAD_DIR = "upload.dir";
  public static final String CFG_STATIC_FILE_CACHE = "static.file.cache";
  public static final String CFG_SERVER_HTTP_PORT = "server.http.port";
  public static final String CFG_SERVER_HTTPS_PORT = "server.https.port";
  public static final String CFG_SERVER_CORS_ALLOWS_ORIGINS = "server.cors.allows.origins";
  public static final String CFG_SERVER_CORS_ALLOWS_CREDENTIALS = "server.cors.allows.credentials";
  public static final String CFG_WEBSOCKET_PATH = "server.websocket.path";
  public static final String CFG_THROTTLE = "server.throttle";
  public static final String CFG_POSTDATA_USEDISK_THRESHOLD = "server.postData.useDisk.threshold";
  public static final String CFG_SECURITY_RESOURCES = "security.resources";
  public static final String CFG_SECURITY_NON_RESOURCES = "security.non-resources";

  public static final char SLASH = '/';
  public static final String PATH_SEPARATOR = "/";
  public static final String PATH_FAVICON = "/favicon.ico";
  public static final String PATH_400 = "/400";
  public static final String PATH_404 = "/404";
  public static final String PATH_500 = "/500";
  public static final String PATH_ERROR = "/error";
  public static final String PATH_THEME = "/theme";
  public static final String PATH_INDEX = "index";
  public static final String PATH_INDEX_HTML = "index.html";

  public static final String PREFIX_CLASSPATH = "classpath:";
  public static final int PREFIX_CLASSPATH_LEN = PREFIX_CLASSPATH.length();

  public static final String KEY_URL = "url";
  public static final String KEY_REFERER = "referer";
  public static final String KEY_ERROR = "error";

  public static final char DOT = '.';
  public static final String EMPTY = "";
  public static final String SIGN_DOT = ".";
  public static final String SIGN_COMMA = ",";
  public static final String SIGN_SEMI_COLON = ";";
  public static final String SIGN_COLON = ":";
  public static final String SIGN_EXCLAMATION = "!";
  public static final String SIGN_QUESTION = "?";
  public static final String SIGN_AND = "&";
  public static final String SIGN_STAR = "*";
  public static final String SIGN_EQUAL = "=";

  public static final int DEFAULT_HTTP_PORT = 7000;
  public static final String UTF_8 = "UTF-8";

  public static final String HEADER_CACHE_CONTROL_NO_CACHE = "no-cache, no-store, must-revalidate";
  public static final String HEADER_EXPIRE_NOW = "0";
}
