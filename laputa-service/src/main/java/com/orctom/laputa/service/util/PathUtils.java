package com.orctom.laputa.service.util;

import java.util.regex.Pattern;

import static com.orctom.laputa.service.Constants.SIGN_DOT;

public abstract class PathUtils {

  private static final Pattern PATTERN_SLASHES = Pattern.compile("/+");
  private static final String SLASH = "/";

  public static String getExtension(String path) {
    int lastDotIndex = path.lastIndexOf(SIGN_DOT);
    if (lastDotIndex > 1 && lastDotIndex < path.length() - 1) {
      return path.substring(lastDotIndex);
    } else {
      return null;
    }
  }

  public static String removeDuplicatedSlashes(String path) {
    if (null == path || 0 == path.length()) {
      return path;
    }
    return PATTERN_SLASHES.matcher(path).replaceAll(SLASH);
  }
}
