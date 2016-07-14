package com.orctom.laputa.util;

import com.orctom.laputa.exception.IllegalArgException;

/**
 * Bean utils that provides limited features to manipulate beans
 * Created by hao on 7/11/16.
 */
public class BeanUtils {

  public <T> T newInstance(Class<T> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalArgException(e.getMessage(), e);
    }
  }

  public void invoke(Object obj, String property, Object value) {

  }
}
