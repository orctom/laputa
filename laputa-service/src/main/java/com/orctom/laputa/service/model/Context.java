package com.orctom.laputa.service.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Request processing context.
 * <ol>
 *   <li>Carries some context info</li>
 *   <li>Redirection supports</li>
 * </ol>
 */
public class Context {

  private String path;
  private Map<String, Object> data = new HashMap<>();
  private Set<ResponseCookie> cookies = new HashSet<>();

  public Context(String path) {
    this.path = path;
  }

  private String redirectTo;

  public String getPath() {
    return path;
  }

  public void setData(String key, Object value) {
    data.put(key, value);
  }

  public Map<String, Object> getData() {
    return data;
  }

  public Set<ResponseCookie> getCookies() {
    return cookies;
  }

  public void setCookie(String name, String value) {
    this.cookies.add(new ResponseCookie(name, value));
  }

  public void setCookie(String name, String value, long maxAge, boolean secure, boolean httpOnly) {
    this.cookies.add(new ResponseCookie(name, value, maxAge, secure, httpOnly));
  }

  public void setRedirectTo(String location) {
    this.redirectTo = location;
  }

  public String getRedirectTo() {
    return redirectTo;
  }
}
