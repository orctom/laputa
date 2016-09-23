package com.orctom.laputa.server.util;

import com.google.common.base.Splitter;
import com.orctom.laputa.server.annotation.Param;
import com.orctom.laputa.server.config.ServiceConfig;
import com.orctom.utils.ClassUtils;
import com.typesafe.config.Config;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utils to set/get properties or invoke methods dynamically
 * Created by hao on 1/5/16.
 */
public abstract class ArgsResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArgsResolver.class);
  private static final String DATE_PATTERN = "date.pattern";
  private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd,yyyyMMdd,yyyy-MM-dd HH:mm:ss";
  private static final String DOT = ".";
  private static final String EMPTY = "EMPTY";

  static {
    Config config = ServiceConfig.getInstance().getConfig();
    String pattern;
    try {
      pattern = config.getString(DATE_PATTERN);
    } catch (Exception e) {
      LOGGER.warn("Missing config for `date.pattern`, using default: {}", DEFAULT_DATE_PATTERN);
      pattern = DEFAULT_DATE_PATTERN;
    }

    List<String> splits = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(pattern);
    String[] patterns = splits.toArray(new String[splits.size()]);

    DateConverter converter = new DateConverter();
    converter.setPatterns(patterns);
    ConvertUtils.register(converter, Date.class);
  }

  public static Object[] resolveArgs(Method method, Map<String, String> paramValues) {
    Parameter[] methodParameters = method.getParameters();
    if (0 == methodParameters.length) {
      return null;
    }

    Object[] args = new Object[methodParameters.length];

    Map<Parameter, Integer> complexParameters = new HashMap<>();
    int resolved = resolveSimpleTypeArgs(paramValues, methodParameters, args, complexParameters);

    if (methodParameters.length != resolved) { // complex types exist
      resolveComplexTypeArgs(paramValues, args, complexParameters);
    }

    return args;
  }

  private static int resolveSimpleTypeArgs(Map<String, String> paramValues,
                                           Parameter[] methodParameters,
                                           Object[] args,
                                           Map<Parameter, Integer> complexParameters) {
    int count = 0;
    for (int i = 0; i < methodParameters.length; i++) {
      Parameter parameter = methodParameters[i];
      Class<?> type = parameter.getType();

      if (ClassUtils.isSimpleValueType((type))) {
        String paramName = parameter.getAnnotation(Param.class).value();
        args[i] = resolveSimpleTypeValue(paramValues, paramName, type);
        count++;
      } else {
        complexParameters.put(parameter, i);
      }
    }

    return count;
  }

  private static void resolveComplexTypeArgs(Map<String, String> paramValues,
                                            Object[] args,
                                            Map<Parameter, Integer> complexParameters) {
    for (Map.Entry<Parameter, Integer> entry : complexParameters.entrySet()) {
      Parameter parameter = entry.getKey();
      String paramName = parameter.getAnnotation(Param.class).value();
      Class<?> type = entry.getKey().getType();
      int index = entry.getValue();

      Map<String, String> params = retrieveParams(paramValues, paramName);
      if (params.isEmpty()) {
        params = paramValues;
      }

      Object arg = generateAndPopulateArg(params, type);

      if (null != arg) {
        args[index] = arg;
      }
    }
  }

  private static Object generateAndPopulateArg(Map<String, String> paramValues, Class<?> type) {
    Object bean = createNewInstance(type);
    try {
      BeanUtils.populate(bean, paramValues);
      return bean;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  private static Map<String, String> getNestedParamValues(Map<String, String> paramValues) {
    return paramValues.entrySet().stream()
        .filter(entry -> entry.getKey().contains(DOT))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private static Object createNewInstance(Class<?> type) {
    try {
      Object instance = type.newInstance();
      initializeProperties(instance, type);
      return instance;
    } catch (Exception e) {
      String msg = type + ", due to: " + e.getMessage();
      throw new IllegalArgumentException(msg, e);
    }
  }

  private static Object resolveSimpleTypeValue(
      Map<String, String> paramValues, String paramName, Class<?> type) {
    String value = paramValues.remove(paramName);
    if (null == value) {
      throw new IllegalArgumentException("Missing param: " + paramName);
    }
    if (String.class.isAssignableFrom(type)) {
      return value;
    } else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
      return Integer.valueOf(value);
    } else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
      return Double.valueOf(value);
    } else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
      return Float.valueOf(value);
    } else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
      return Long.valueOf(value);
    } else {
      throw new IllegalArgumentException("Unsupported param type" + type + " " + paramName);
    }
  }

  private static void initializeProperties(Object bean, Class<?> type) {
    Set<Field> fields = new HashSet<>();
    fields.addAll(Arrays.asList(type.getFields()));
    fields.addAll(Arrays.asList(type.getDeclaredFields()));
    for (Field field : fields) {
      if (EMPTY.equals(field.getName())) {
        continue;
      }

      Class<?> propertyType = field.getType();
      if (ClassUtils.isSimpleValueType(propertyType)) {
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

  private static Map<String, String> retrieveParams(Map<String, String> paramValues, String paramName) {
    return paramValues
        .entrySet()
        .stream()
        .filter(item -> item.getKey().startsWith(paramName) && item.getKey().length() > paramName.length())
        .collect(Collectors.toMap(
            item -> item.getKey().substring(item.getKey().indexOf(paramName) + paramName.length() + 1),
            Map.Entry::getValue));
  }
}
