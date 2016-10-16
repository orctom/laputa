package com.orctom.laputa.server.util;

import com.orctom.laputa.exception.IllegalArgException;
import com.orctom.laputa.server.annotation.DefaultValue;
import com.orctom.laputa.server.annotation.Param;
import com.orctom.laputa.server.model.RequestWrapper;
import com.orctom.laputa.utils.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamResolver {

  public static Map<String, String> extractParams(Method method, String pattern, RequestWrapper requestWrapper) {
    String path = requestWrapper.getPath();
    Map<String, List<String>> queryParameters = requestWrapper.getParams();

    Map<String, String> params = new HashMap<>();

    params.putAll(extractDefaultValues(method));
    params.putAll(extractQueryParams(queryParameters));
    params.putAll(extractPathParams(pattern, path));

    return params;
  }

  public static Map<String, String> extractDefaultValues(Method method) {
    Parameter[] methodParameters = method.getParameters();
    if (0 == methodParameters.length) {
      return Collections.emptyMap();
    }

    Map<String, String> params = new HashMap<>();
    for (Parameter parameter : methodParameters) {
      Param param = parameter.getAnnotation(Param.class);
      if (null == param) {
        throw new IllegalArgException("Missing @Param annotation at " + method.toString());
      }
      DefaultValue defaultValue = parameter.getAnnotation(DefaultValue.class);
      if (null != defaultValue && ClassUtils.isSimpleValueType(parameter.getType())) {
        params.put(param.value(), defaultValue.value());
      }
    }

    return params;
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
    String[] patternItems = pattern.split("/");
    String[] pathItems = path.split("/");
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
}
