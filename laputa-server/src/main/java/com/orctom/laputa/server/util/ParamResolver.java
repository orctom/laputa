package com.orctom.laputa.server.util;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.orctom.laputa.server.annotation.DefaultValue;
import com.orctom.laputa.server.annotation.Param;
import com.orctom.laputa.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParamResolver {

  private static final Pattern TOKEN_START = Pattern.compile("[\\{]");
  private static final Pattern TOKEN_END = Pattern.compile("[\\}]");

  public static Map<String, String> extractParams(
      Method method, String pattern, String path, String queryStr) {
    Map<String, String> params = new HashMap<>();

    params.putAll(extractDefaultValues(method));
    params.putAll(extractQueryParams(queryStr));
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
        throw new IllegalArgumentException("Missing @Param annotation at " + method.toString());
      }
      DefaultValue defaultValue = parameter.getAnnotation(DefaultValue.class);
      if (null != defaultValue && ClassUtils.isSimpleValueType(parameter.getType())) {
        params.put(param.value(), defaultValue.value());
      }
    }

    return params;
  }

  public static Map<String, String> extractQueryParams(String queryStr) {
    if (Strings.isNullOrEmpty(queryStr)) {
      return Collections.emptyMap();
    }

    return Arrays.stream(queryStr.split("&"))
        .map(item -> item.split("="))
        .collect(Collectors.toMap(p -> p[0], p -> p[1]));
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

  public static void validate(String pattern) {
    Splitter.on("/").omitEmptyStrings().split(pattern).forEach(item -> {
      if (TOKEN_START.matcher(item).replaceAll("").length() > 1) {
        throw new IllegalArgumentException("Unsupported URL pattern: " + pattern);
      }
      if (TOKEN_END.matcher(item).replaceAll("").length() > 1) {
        throw new IllegalArgumentException("Unsupported URL pattern: " + pattern);
      }
    });
  }
}
