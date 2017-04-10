package com.orctom.laputa.service.model;

import com.google.common.base.Strings;
import io.netty.handler.codec.http.cookie.Cookie;

public class ResponseCookie implements Cookie {

  private final String name;
  private String value;
  private boolean wrap;
  private String domain;
  private String path = "/";
  private long maxAge = UNDEFINED_MAX_AGE;
  private boolean secure = true;
  private boolean httpOnly = true;

  public ResponseCookie(String name, String value) {
    if (Strings.isNullOrEmpty(name)) {
      throw new NullPointerException("Cookie name is null.");
    }
    if (Strings.isNullOrEmpty(value)) {
      throw new NullPointerException("Cookie value is null.");
    }
    this.name = name;
    this.value = value;
  }

  public ResponseCookie(String name, String value, long maxAge, boolean secure, boolean httpOnly) {
    this(name, value);
    this.maxAge = maxAge;
    this.secure = secure;
    this.httpOnly = httpOnly;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String value() {
    return this.value;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean wrap() {
    return wrap;
  }

  @Override
  public void setWrap(boolean wrap) {
    this.wrap = wrap;
  }

  @Override
  public String domain() {
    return domain;
  }

  @Override
  public void setDomain(String domain) {
    this.domain = domain;
  }

  @Override
  public String path() {
    return path;
  }

  @Override
  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public long maxAge() {
    return maxAge;
  }

  @Override
  public void setMaxAge(long maxAge) {
    this.maxAge = maxAge;
  }

  @Override
  public boolean isSecure() {
    return secure;
  }

  @Override
  public void setSecure(boolean secure) {
    this.secure = secure;
  }

  @Override
  public boolean isHttpOnly() {
    return httpOnly;
  }

  @Override
  public void setHttpOnly(boolean httpOnly) {
    this.httpOnly = httpOnly;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResponseCookie that = (ResponseCookie) o;

    return name != null ? name.equals(that.name) : that.name == null;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  @Override
  public int compareTo(Cookie o) {
    return name().compareTo(o.name());
  }
}
