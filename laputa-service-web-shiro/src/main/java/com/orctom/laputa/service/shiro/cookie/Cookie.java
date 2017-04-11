package com.orctom.laputa.service.shiro.cookie;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

public interface Cookie {

  public static final String DELETED_COOKIE_VALUE = "deleteMe";

  public static final int TWO_WEEKS = 60 * 60 * 24 * 14;

  public static final String ROOT_PATH = "/";

  String getName();

  void setName(String name);

  String getValue();

  void setValue(String value);

  String getComment();

  void setComment(String comment);

  String getDomain();

  void setDomain(String domain);

  int getMaxAge();

  void setMaxAge(int maxAge);

  String getPath();

  void setPath(String path);

  boolean isSecure();

  void setSecure(boolean secure);

  int getVersion();

  void setVersion(int version);

  void setHttpOnly(boolean httpOnly);

  boolean isHttpOnly();

  void saveTo(RequestWrapper requestWrapper, ResponseWrapper responseWrapper);

  void removeFrom(RequestWrapper requestWrapper, ResponseWrapper responseWrapper);

  String readValue(RequestWrapper requestWrapper, ResponseWrapper responseWrapper);
}
