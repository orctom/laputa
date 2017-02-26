package com.orctom.laputa.service.util;

import com.orctom.laputa.service.annotation.Param;
import com.orctom.laputa.service.exception.ParameterValidationException;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.utils.ClassUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utils to set/get properties or invoke methods dynamically
 * Created by hao on 1/5/16.
 */
public abstract class ArgsResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArgsResolver.class);
  private static final String DOT = ".";

  public static Object[] resolveArgs(Parameter[] methodParameters, Map<String, String> paramValues, Context ctx) {
    if (0 == methodParameters.length) {
      return null;
    }

    Object[] args = new Object[methodParameters.length];

    Map<Parameter, Integer> complexParameters = new HashMap<>();
    int resolved = resolveSimpleTypeArgs(paramValues, methodParameters, args, complexParameters);

    if (methodParameters.length != resolved) { // complex types exist
      resolveComplexTypeArgs(paramValues, args, complexParameters, ctx);
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
                                             Map<Parameter, Integer> complexParameters,
                                             Context ctx) {
    for (Map.Entry<Parameter, Integer> entry : complexParameters.entrySet()) {
      Parameter parameter = entry.getKey();
      String paramName = parameter.getAnnotation(Param.class).value();
      Class<?> type = entry.getKey().getType();
      int index = entry.getValue();

      if (Context.class.isAssignableFrom(type)) {
        args[index] = ctx;
        continue;
      }

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
    Object bean = BeanUtil.createNewInstance(type);
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

  private static Object resolveSimpleTypeValue(
      Map<String, String> paramValues, String paramName, Class<?> type) {
    String value = paramValues.remove(paramName);
    try {
      if (String.class.isAssignableFrom(type)) {
        return value;
      } else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
        return null == value ? null : Integer.valueOf(value);
      } else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
        return null == value ? null : Double.valueOf(value);
      } else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
        return null == value ? null : Float.valueOf(value);
      } else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
        return null == value ? null : Long.valueOf(value);
      } else {
        throw new IllegalArgumentException("Unsupported param type" + type + " " + paramName);
      }
    } catch (NumberFormatException e) {
      throw new ParameterValidationException("Invalid param value: " + value + ", is not " + type.getSimpleName());
    }
  }

  private static Map<String, String> retrieveParams(Map<String, String> paramValues, String paramName) {
    return paramValues
        .entrySet()
        .stream()
        .filter(item -> item.getKey().startsWith(paramName) && item.getKey().length() > paramName.length())
        .collect(Collectors.toMap(
            item -> {
              String param = paramName + ".";
              String key = item.getKey();
              int start = key.indexOf(param);
              return start >= 0 ? key.substring(start + param.length()) : key;
            },
            Map.Entry::getValue));
  }
}
