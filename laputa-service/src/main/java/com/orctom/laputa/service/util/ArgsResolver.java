package com.orctom.laputa.service.util;

import com.orctom.laputa.service.exception.ParameterValidationException;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.utils.ClassUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.orctom.laputa.service.Constants.SIGN_DOT;

/**
 * Utils to set/get properties or invoke methods dynamically
 * Created by hao on 1/5/16.
 */
public abstract class ArgsResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArgsResolver.class);

  public static Object[] resolveArgs(Map<String, String> paramValues, Map<String, Class<?>> paramTypes, Context ctx) {
    if (paramTypes.isEmpty()) {
      return null;
    }

    int paramLength = paramTypes.size();
    Object[] args = new Object[paramLength];

    Map<Map.Entry<String, Class<?>>, Integer> complexParameters = new HashMap<>();
    int resolved = resolveSimpleTypeArgs(paramValues, paramTypes, args, complexParameters);

    if (paramLength != resolved) { // complex types exist
      resolveComplexTypeArgs(paramValues, args, complexParameters, ctx);
    }

    return args;
  }

  private static int resolveSimpleTypeArgs(Map<String, String> paramValues,
                                           Map<String, Class<?>> paramTypes,
                                           Object[] args,
                                           Map<Map.Entry<String, Class<?>>, Integer> complexParameters) {
    int count = 0, i = 0;
    for (Map.Entry<String, Class<?>> entry : paramTypes.entrySet()) {
      String paramName = entry.getKey();
      Class<?> type = entry.getValue();
      if (ClassUtils.isSimpleValueType((type))) {
        args[i] = resolveSimpleTypeValue(paramValues, paramName, type);
        count++;
      } else {
        complexParameters.put(entry, i);
      }
      i++;
    }

    return count;
  }

  private static void resolveComplexTypeArgs(Map<String, String> paramValues,
                                             Object[] args,
                                             Map<Map.Entry<String, Class<?>>, Integer> complexParameters,
                                             Context ctx) {
    for (Map.Entry<Map.Entry<String, Class<?>>, Integer> entry : complexParameters.entrySet()) {
      Map.Entry<String, Class<?>> key = entry.getKey();
      String paramName = key.getKey();
      Class<?> type = key.getValue();
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
        .filter(entry -> entry.getKey().contains(SIGN_DOT))
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
              String param = paramName + SIGN_DOT;
              String key = item.getKey();
              int start = key.indexOf(param);
              return start >= 0 ? key.substring(start + param.length()) : key;
            },
            Map.Entry::getValue));
  }
}
