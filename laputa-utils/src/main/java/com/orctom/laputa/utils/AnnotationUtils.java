package com.orctom.laputa.utils;

import java.lang.annotation.Annotation;

public abstract class AnnotationUtils {

  public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationClass) {
    A annotation = clazz.getAnnotation(annotationClass);
    if (null != annotation) {
      return annotation;
    }

    for (Class<?> ifc : clazz.getInterfaces()) {
      annotation = ifc.getAnnotation(annotationClass);
      if (null != annotation) {
        return annotation;
      }
    }

    Class<?> superclass = clazz.getSuperclass();
    if (superclass == null || superclass.equals(Object.class)) {
      return null;
    }

    return findAnnotation(superclass, annotationClass);
  }
}
