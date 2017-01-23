package com.orctom.laputa.utils;

import com.orctom.laputa.exception.ClassLoadingException;
import org.junit.Test;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ClassUtilsTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtilsTest.class);

  @Test
  public void testScanDirectory() {
    try {
      List<Class<?>> classes = ClassUtils.getClassesWithAnnotation(
          "com.orctom.laputa.utils",
          RunListener.ThreadSafe.class
      );
      classes.forEach(clazz -> LOGGER.debug(clazz.getName()));
      assertThat(classes, notNullValue());
      assertThat(classes.size(), greaterThan(0));
    } catch (ClassLoadingException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testScanJar() {
    try {
      List<Class<?>> classes = ClassUtils.getClassesWithAnnotation(
          "org.junit.runner.notification",
          RunListener.ThreadSafe.class
      );
      classes.forEach(clazz -> LOGGER.debug(clazz.getName()));
      assertThat(classes, notNullValue());
      assertThat(classes.size(), greaterThan(0));
    } catch (ClassLoadingException e) {
      e.printStackTrace();
    }
  }
}
