package com.orctom.laputa.service.shiro.cookie;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCookie implements Cookie {

  public static final String DEFAULT_SESSION_ID_NAME = "SESSIONID";

  /**
   * {@code -1}, indicating the cookie should expire when the browser closes.
   */
  public static final int DEFAULT_MAX_AGE = -1;

  /**
   * {@code -1} indicating that no version property should be set on the cookie.
   */
  public static final int DEFAULT_VERSION = -1;

  private static final transient Logger log = LoggerFactory.getLogger(SimpleCookie.class);

  private String name;
  private String value;
  private String comment;
  private String domain;
  private String path;
  private int maxAge;
  private int version;
  private boolean secure;
  private boolean httpOnly;

  public SimpleCookie() {
    this.maxAge = DEFAULT_MAX_AGE;
    this.version = DEFAULT_VERSION;
    this.httpOnly = true; //most of the cookies ever used by Shiro should be as secure as possible.
  }

  public SimpleCookie(String name) {
    this();
    this.name = name;
  }

  public SimpleCookie(Cookie cookie) {
    this.name = cookie.getName();
    this.value = cookie.getValue();
    this.comment = cookie.getComment();
    this.domain = cookie.getDomain();
    this.path = cookie.getPath();
    this.maxAge = Math.max(DEFAULT_MAX_AGE, cookie.getMaxAge());
    this.version = Math.max(DEFAULT_VERSION, cookie.getVersion());
    this.secure = cookie.isSecure();
    this.httpOnly = cookie.isHttpOnly();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (!StringUtils.hasText(name)) {
      throw new IllegalArgumentException("Name cannot be null/empty.");
    }
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(int maxAge) {
    this.maxAge = Math.max(DEFAULT_MAX_AGE, maxAge);
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = Math.max(DEFAULT_VERSION, version);
  }

  public boolean isSecure() {
    return secure;
  }

  public void setSecure(boolean secure) {
    this.secure = secure;
  }

  public boolean isHttpOnly() {
    return httpOnly;
  }

  public void setHttpOnly(boolean httpOnly) {
    this.httpOnly = httpOnly;
  }

  @Override
  public void saveTo(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    String name = getName();
    String value = getValue();
    String comment = getComment();
    String domain = getDomain();
    String path = "/";
    int maxAge = getMaxAge();
    int version = getVersion();
    boolean secure = isSecure();
    boolean httpOnly = isHttpOnly();

    addCookieHeader(responseWrapper, name, value, comment, domain, path, maxAge, version, secure, httpOnly);
  }

  private void addCookieHeader(ResponseWrapper responseWrapper, String name, String value, String comment,
                               String domain, String path, int maxAge, int version,
                               boolean secure, boolean httpOnly) {

    responseWrapper.setCookie(name, value, maxAge, secure, httpOnly, domain);
  }

  @Override
  public void removeFrom(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    String name = getName();
    String value = DELETED_COOKIE_VALUE;
    String comment = null; //don't need to add extra size to the response - comments are irrelevant for deletions
    String domain = getDomain();
    String path = "/";
    int maxAge = 0; //always zero for deletion
    int version = getVersion();
    boolean secure = isSecure();
    boolean httpOnly = false; //no need to add the extra text, plus the value 'deleteMe' is not sensitive at all

    addCookieHeader(responseWrapper, name, value, comment, domain, path, maxAge, version, secure, httpOnly);

    log.trace("Removed '{}' cookie by setting maxAge=0", name);
  }

  @Override
  public String readValue(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    String name = getName();
    if (null == requestWrapper.getCookies() || requestWrapper.getCookies().isEmpty()) {
      return null;
    }

    return requestWrapper.getCookies().get(name);
  }
}
