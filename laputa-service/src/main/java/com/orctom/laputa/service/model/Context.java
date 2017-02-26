package com.orctom.laputa.service.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Request processing context.
 * <ol>
 *   <li>Carries some context info</li>
 *   <li>Redirection supports</li>
 * </ol>
 */
public class Context {

  private Map<String, Object> data = new HashMap<>();

  private String redirectTo;

  public void put(String key, Object value) {
    data.put(key, value);
  }

  public Object get(String key) {
    return data.get(key);
  }

  public void redirectTo(String location) {
    this.redirectTo = location;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public String getRedirectTo() {
    return redirectTo;
  }
}
