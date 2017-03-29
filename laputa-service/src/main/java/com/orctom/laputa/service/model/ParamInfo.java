package com.orctom.laputa.service.model;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParamInfo {

  private String defaultValue;
  private Class<?> type;
  private Map<Class<? extends Annotation>, Annotation> annotations = Collections.emptyMap();

  public ParamInfo() {
  }

  public ParamInfo(Class<?> type) {
    this.type = type;
  }

  public ParamInfo(String defaultValue, Class<?> type, Annotation[] annotations) {
    this.defaultValue = defaultValue;
    this.type = type;
    if (null != annotations && 0 != annotations.length) {
      this.annotations = new HashMap<>(annotations.length * 2);
      for (Annotation annotation : annotations) {
        this.annotations.put(annotation.getClass(), annotation);
      }
    }
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public Class<?> getType() {
    return type;
  }

  public  Map<Class<? extends Annotation>, Annotation> getAnnotations() {
    return annotations;
  }

  @SuppressWarnings("unchecked")
  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    Annotation annotation = annotations.get(annotationClass);
    return null == annotation ? null : (T) annotation;
  }
}
