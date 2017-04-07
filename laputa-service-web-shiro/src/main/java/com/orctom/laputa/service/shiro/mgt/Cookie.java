package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

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

  void saveTo(RequestWrapper requestWrapper, Context context);

  void removeFrom(RequestWrapper requestWrapper, Context context);

  String readValue(RequestWrapper requestWrapper, Context context);
}
