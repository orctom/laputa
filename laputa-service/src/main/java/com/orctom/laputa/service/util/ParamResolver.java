package com.orctom.laputa.service.util;

import com.orctom.laputa.exception.IllegalConfigException;
import com.orctom.laputa.service.annotation.Cookie;
import com.orctom.laputa.service.annotation.Cookies;
import com.orctom.laputa.service.annotation.Data;
import com.orctom.laputa.service.annotation.DefaultValue;
import com.orctom.laputa.service.annotation.HttpHeader;
import com.orctom.laputa.service.annotation.HttpHeaders;
import com.orctom.laputa.service.annotation.Param;
import com.orctom.laputa.service.model.ParamInfo;
import com.orctom.laputa.service.model.RequestMapping;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.utils.ClassUtils;
import org.apache.commons.collections.map.HashedMap;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.orctom.laputa.service.Constants.PATH_SEPARATOR;
import static com.orctom.laputa.service.Constants.SIGN_DOT;

public class ParamResolver {

  public static Map<String, String> extractParams(RequestMapping mapping, RequestWrapper requestWrapper) {
    String path = requestWrapper.getPath();
    Map<String, List<String>> queryParameters = requestWrapper.getParams();

    String pattern = mapping.getUriPattern();
    Map<String, String> params = new HashMap<>();

    params.putAll(extractDefaultValues(mapping));
    params.putAll(extractQueryParams(queryParameters));
    params.putAll(extractPathParams(pattern, path));

    return params;
  }

  public static Map<String, String> extractDefaultValues(RequestMapping mapping) {
    Map<String,  String> defaultValues = new HashMap<>();
    mapping.getHandlerParameters().forEach((key, value) -> defaultValues.put(key, value.getDefaultValue()));
    return defaultValues;
  }

  public static Map<String, String> extractQueryParams(Map<String, List<String>> queryParameters) {
    if (null == queryParameters || queryParameters.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, String> queryParams = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : queryParameters.entrySet()) {
      List<String> values = entry.getValue();
      if (null == values || values.isEmpty()) {
        continue;
      }

      int sizeOfValues = values.size();

      if (1 == sizeOfValues) {
        queryParams.put(entry.getKey(), values.get(0));
      } else {
        for (int i = 0; i < sizeOfValues; i++) {
          queryParams.put(entry.getKey() + "[" + i + "]", values.get(i));
        }
      }
    }

    return queryParams;
  }

  public static Map<String, String> extractPathParams(String pattern, String path) {
    if (!pattern.contains("{")) {
      return Collections.emptyMap();
    }

    Map<String, String> params = new HashMap<>();
    String[] patternItems = pattern.split(PATH_SEPARATOR);
    String[] pathItems = removeExtension(path).split(PATH_SEPARATOR);
    for (int i = 0; i < patternItems.length; i++) {
      String patternItem = patternItems[i];
      String pathItem = pathItems[i];

      int patternItemLen = patternItem.length();
      if (0 == patternItemLen) {
        continue;
      }

      int tokenStartIndex = patternItem.indexOf("{");
      if (tokenStartIndex < 0) {
        continue;
      }

      int tokenEndIndex = patternItem.indexOf("}");
      String varName = patternItem.substring(tokenStartIndex + 1, tokenEndIndex);
      int varValueEndIndex = pathItem.length() - (patternItemLen - tokenEndIndex) + 1;
      String varValue = pathItem.substring(tokenStartIndex, varValueEndIndex);
      params.put(varName, varValue);
    }
    return params;
  }

  private static String removeExtension(String path) {
    int dotIndex = path.indexOf(SIGN_DOT);
    if (-1 == dotIndex) {
      return path;
    }
    return path.substring(0, dotIndex);
  }

  public static String getDefaultValue(Parameter parameter) {
    DefaultValue defaultValue = parameter.getAnnotation(DefaultValue.class);
    if (null != defaultValue && ClassUtils.isSimpleValueType(parameter.getType())) {
      return defaultValue.value();
    }
    return null;
  }

  public static String getParamName(Parameter parameter, Method handlerMethod) {
    Param param = parameter.getAnnotation(Param.class);
    if (null != param) {
      return param.value();
    }

    Cookies cookies = parameter.getAnnotation(Cookies.class);
    if (null != cookies) {
      return "_cookies_";
    }

    Cookie cookie = parameter.getAnnotation(Cookie.class);
    if (null != cookie) {
      return "_cookie_";
    }

    HttpHeaders httpHeaders = parameter.getAnnotation(HttpHeaders.class);
    if (null != httpHeaders) {
      return "_httpHeaders_";
    }

    HttpHeader httpHeader = parameter.getAnnotation(HttpHeader.class);
    if (null != httpHeader) {
      return "_httpHeader_";
    }

    if (parameter.isAnnotationPresent(Data.class)) {
      throw new IllegalConfigException("Only one param is allowed when using @Data annotation, at " + handlerMethod.toString());
    }

    throw new IllegalConfigException("Missing @Param annotation at " + handlerMethod.toString());
  }
}
