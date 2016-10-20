package com.orctom.laputa.server.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.orctom.laputa.utils.ClassUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public abstract class BeanUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);
  private static final String EMPTY = "EMPTY";

  private static LoadingCache<Class<?>, Set<Field>> fieldsCache = CacheBuilder.newBuilder()
      .softValues()
      .build(new CacheLoader<Class<?>, Set<Field>>() {
        @Override
        public Set<Field> load(Class<?> type) throws Exception {
          Set<Field> fields = new HashSet<>();
          fields.addAll(Arrays.asList(type.getFields()));
          fields.addAll(Arrays.asList(type.getDeclaredFields()));
          return fields;
        }
      });

  public static Object createNewInstance(Class<?> type) {
    try {
      Object instance = type.newInstance();
      initializeProperties(instance, type);
      return instance;
    } catch (Exception e) {
      String msg = type + ", due to: " + e.getMessage();
      throw new IllegalArgumentException(msg, e);
    }
  }

  public static void initializeProperties(Object bean, Class<?> type) {
    Set<Field> fields = null;
    try {
      fields = fieldsCache.get(type);
    } catch (ExecutionException e) {
      LOGGER.error(e.getMessage(), e);
    }

    if (null == fields || fields.isEmpty()) {
      return;
    }

    for (Field field : fields) {
      if (EMPTY.equals(field.getName()) || field.isSynthetic()) {
        continue;
      }

      Class<?> propertyType = field.getType();
      if (ClassUtils.isSimpleValueType(propertyType) || propertyType.isAssignableFrom(File.class)) {
        continue;
      }
      Object property = createNewInstance(propertyType);
      try {
        PropertyUtils.setProperty(bean, field.getName(), property);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        LOGGER.warn(e.getMessage(), e);
      }
    }
  }
}