package com.orctom.laputa.service.util;

import com.orctom.laputa.service.annotation.Cookie;
import com.orctom.laputa.service.annotation.Cookies;
import com.orctom.laputa.service.annotation.HttpHeader;
import com.orctom.laputa.service.annotation.HttpHeaders;
import com.orctom.laputa.service.exception.ParameterValidationException;
import com.orctom.laputa.service.model.Messenger;
import com.orctom.laputa.service.model.ParamInfo;
import com.orctom.laputa.service.model.RequestWrapper;
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

  public static Object[] resolveArgs(Map<String, String> paramValues,
                                     Map<String, ParamInfo> parameters,
                                     RequestWrapper requestWrapper,
                                     Messenger messenger) {
    if (parameters.isEmpty()) {
      return null;
    }

    int paramLength = parameters.size();
    Object[] args = new Object[paramLength];

    Map<Map.Entry<String, ParamInfo>, Integer> complexParameters = new HashMap<>();
    int resolved = resolveSimpleTypeArgs(paramValues, parameters, requestWrapper, args, complexParameters);

    if (paramLength != resolved) { // complex types exist
      resolveComplexTypeArgs(paramValues, args, complexParameters, messenger);
    }

    return args;
  }

  private static int resolveSimpleTypeArgs(Map<String, String> paramValues,
                                           Map<String, ParamInfo> parameters,
                                           RequestWrapper requestWrapper,
                                           Object[] args,
                                           Map<Map.Entry<String, ParamInfo>, Integer> complexParameters) {
    int count = 0, i = 0;
    for (Map.Entry<String, ParamInfo> entry : parameters.entrySet()) {
      String paramName = entry.getKey();
      ParamInfo paramInfo = entry.getValue();

      Cookies cookies = paramInfo.getAnnotation(Cookies.class);
      if (null != cookies) {
        args[i++] = requestWrapper.getCookies();
        count++;
        continue;
      }

      Cookie cookie = paramInfo.getAnnotation(Cookie.class);
      if (null != cookie) {
        String cookieValue = requestWrapper.getCookies().get(cookie.value());
        args[i++] = null == cookieValue ? paramInfo.getDefaultValue() : cookieValue;
        count++;
        continue;
      }

      HttpHeaders httpHeaders = paramInfo.getAnnotation(HttpHeaders.class);
      if (null != httpHeaders) {
        args[i++] = requestWrapper.getHeaders().entries().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        count++;
        continue;
      }

      HttpHeader httpHeader = paramInfo.getAnnotation(HttpHeader.class);
      if (null != httpHeader) {
        String httpHeaderValue = requestWrapper.getHeaders().get(httpHeader.value());
        args[i++] = null == httpHeaderValue ? paramInfo.getDefaultValue() : httpHeaderValue;
        count++;
        continue;
      }

      Class<?> type = paramInfo.getType();
      if (ClassUtils.isSimpleValueType((type))) {
        args[i++] = resolveSimpleTypeValue(paramValues, paramName, type);
        count++;
      } else {
        complexParameters.put(entry, i++);
      }
    }

    return count;
  }

  private static void resolveComplexTypeArgs(Map<String, String> paramValues,
                                             Object[] args,
                                             Map<Map.Entry<String, ParamInfo>, Integer> complexParameters,
                                             Messenger messenger) {
    for (Map.Entry<Map.Entry<String, ParamInfo>, Integer> entry : complexParameters.entrySet()) {
      Map.Entry<String, ParamInfo> key = entry.getKey();
      String paramName = key.getKey();
      ParamInfo paramInfo = key.getValue();
      Class<?> type = paramInfo.getType();
      int index = entry.getValue();

      if (Messenger.class.isAssignableFrom(type)) {
        args[index] = messenger;
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

  private static Object resolveSimpleTypeValue(Map<String, String> paramValues,
                                               String paramName,
                                               Class<?> type) {
    String value = paramValues.remove(paramName);
    try {
      if (String.class.isAssignableFrom(type)) {
        return value;
      } else if (Integer.class.isAssignableFrom(type)) {
        return null == value ? null : Integer.valueOf(value);
      } else if (int.class.isAssignableFrom(type)) {
        return null == value ? 0 : Integer.valueOf(value);
      } else if (Double.class.isAssignableFrom(type)) {
        return null == value ? null : Double.valueOf(value);
      } else if (double.class.isAssignableFrom(type)) {
        return null == value ? 0.0d : Double.valueOf(value);
      } else if (Float.class.isAssignableFrom(type)) {
        return null == value ? null : Float.valueOf(value);
      } else if (float.class.isAssignableFrom(type)) {
        return null == value ? 0.0F : Float.valueOf(value);
      } else if (Long.class.isAssignableFrom(type)) {
        return null == value ? null : Long.valueOf(value);
      } else if (long.class.isAssignableFrom(type)) {
        return null == value ? 0 : Long.valueOf(value);
      } else if (Boolean.class.isAssignableFrom(type)) {
        return null == value ? null : Boolean.valueOf(value);
      } else if (boolean.class.isAssignableFrom(type)) {
        return null == value ? false : Boolean.valueOf(value);
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
