package com.orctom.laputa.server.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaiveBeanFactory implements BeanFactory {

  private static Map<Class<?>, Object> cache = new HashMap<>();

  @Override
  public <T> T getInstance(Class<T> clazz) {
    Object instance = cache.get(clazz);
    if (null == instance) {
      try {
        instance = clazz.newInstance();
        cache.put(clazz, instance);
      } catch (Exception e) {
        return null;
      }
    }
    return (T) instance;
  }

  @Override
  public <T> List<T> getInstances(Class<T> type) {
    return null;
  }
}
