package com.orctom.laputa.service.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Messenger from controller to response
 */
public class Messenger {

  private Map<String, Object> data;
  private Set<ResponseCookie> cookies;
  private String redirectTo;

  public void setData(String key, Object value) {
    if (null == data) {
      data = new HashMap<>();
    }
    data.put(key, value);
  }

  public Map<String, Object> getData() {
    return data;
  }

  public Set<ResponseCookie> getCookies() {
    return cookies;
  }

  public void setCookie(String name, String value) {
    ensureCookieSet();
    this.cookies.add(new ResponseCookie(name, value));
  }

  public void setCookie(String name, String value, long maxAge, boolean secure, boolean httpOnly) {
    ensureCookieSet();
    this.cookies.add(new ResponseCookie(name, value, maxAge, secure, httpOnly));
  }

  public void setCookie(String name, String value, long maxAge, boolean secure, boolean httpOnly, String domain) {
    ensureCookieSet();
    ResponseCookie cookie = new ResponseCookie(name, value, maxAge, secure, httpOnly);
    cookie.setDomain(domain);
    this.cookies.add(cookie);
  }

  public void addCookie(Set<ResponseCookie> cookies) {
    if (null == cookies || cookies.isEmpty()) {
      return;
    }

    ensureCookieSet();
    this.cookies.addAll(cookies);
  }

  private void ensureCookieSet() {
    if (null == cookies) {
      cookies = new HashSet<>();
    }
  }

  public void setRedirectTo(String location) {
    this.redirectTo = location;
  }

  public String getRedirectTo() {
    return redirectTo;
  }
}
