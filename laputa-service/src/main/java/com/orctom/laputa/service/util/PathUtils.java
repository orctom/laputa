package com.orctom.laputa.service.util;

import static com.orctom.laputa.service.Constants.SIGN_DOT;

public abstract class PathUtils {

  public static String getExtension(String path) {
    int lastDotIndex = path.lastIndexOf(SIGN_DOT);
    if (lastDotIndex > 1 && lastDotIndex < path.length() - 1) {
      return path.substring(lastDotIndex);
    } else {
      return null;
    }
  }
}
