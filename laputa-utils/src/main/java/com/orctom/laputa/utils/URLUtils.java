package com.orctom.laputa.utils;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public abstract class URLUtils {

  private static final Pattern PATTERN_AND = Pattern.compile("&");
  private static final Pattern PATTERN_DOUBLE_SLASHES = Pattern.compile("(?<!(http:|https:))[/+]+");

  public static Map<String, List<String>> parseQueryString(URI uri) {
    return PATTERN_AND.splitAsStream(uri.getQuery())
        .map(s -> Arrays.copyOf(s.split("="), 2))
        .collect(Collectors.groupingBy(s -> s[0], Collectors.mapping(s -> s[1], toList())));
  }

  public static Map<String, List<String>> parseQueryString(String queryString) {
    return PATTERN_AND.splitAsStream(queryString)
          .map(s -> Arrays.copyOf(s.split("="), 2))
        .collect(Collectors.groupingBy(s -> s[0], Collectors.mapping(s -> s[1], toList())));
  }

  public static String removeDoubleSlashes(String url) {
    return PATTERN_DOUBLE_SLASHES.matcher(url).replaceAll("/");
  }
}
