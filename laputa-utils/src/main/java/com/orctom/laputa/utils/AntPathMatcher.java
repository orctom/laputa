package com.orctom.laputa.utils;

public abstract class AntPathMatcher {

  private static final char ASTERISK = '*';
  private static final char QUESTION = '?';
  private static final char BLANK = ' ';
  private static final int ASCII_CASE_DIFFERENCE_VALUE = 32;

  private static final char PATH_SEPARATOR = '/';
  private static final boolean IGNORE_CASE = false;
  private static final boolean TRIM_TOKENS = false;

  public static boolean isMatch(final String pattern, final String path) {
    if (pattern.isEmpty()) {
      return path.isEmpty();
    }

    if (path.isEmpty() && pattern.charAt(0) == PATH_SEPARATOR) {
      return true;
    }

    final char patternStart = pattern.charAt(0);
    if (ASTERISK == patternStart) {
      if (1 == pattern.length()) {
        return true;

      } else if (doubleAsteriskMatch(pattern, path)) {
        return true;
      }

      int start = 0;
      while (start < path.length()) {
        if (isMatch(pattern.substring(1), path.substring(start))) {
          return true;
        }
        start++;
      }
      return isMatch(pattern.substring(1), path.substring(start));
    }

    int pointer = skipBlanks(path);

    return !path.isEmpty() && (equal(path.charAt(pointer), patternStart) || patternStart == QUESTION)
        && isMatch(pattern.substring(1), path.substring(pointer + 1));
  }

  private static boolean doubleAsteriskMatch(final String pattern, final String path) {
    if (pattern.charAt(1) != ASTERISK) {
      return false;
    } else if (pattern.length() > 2) {
      return isMatch(pattern.substring(3), path);
    }

    return false;
  }

  private static int skipBlanks(final String path) {
    int pointer = 0;
    if (TRIM_TOKENS) {
      while (!path.isEmpty() && pointer < path.length() && path.charAt(pointer) == BLANK) {
        pointer++;
      }
    }
    return pointer;
  }

  private static boolean equal(final char pathChar, final char patternChar) {
    if (IGNORE_CASE) {
      return pathChar == patternChar ||
          ((pathChar > patternChar) ?
              pathChar == patternChar + ASCII_CASE_DIFFERENCE_VALUE :
              pathChar == patternChar - ASCII_CASE_DIFFERENCE_VALUE);
    }
    return pathChar == patternChar;
  }
}
