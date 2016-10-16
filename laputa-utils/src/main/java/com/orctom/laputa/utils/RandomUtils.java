package com.orctom.laputa.utils;

import java.util.Random;

public abstract class RandomUtils {

  private static final String ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
  private static final String NUMERIC = "1234567890";

  private static final int ALPHA_LENGTH = ALPHA.length();
  private static final int ALPHANUMERIC_LENGTH = ALPHANUMERIC.length();
  private static final int NUMERIC_LENGTH = NUMERIC.length();

  private static final Random random = new Random();

  public static String randomAlpha(int length) {
    StringBuilder str = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      str.append(ALPHA.charAt(random.nextInt(ALPHA_LENGTH)));
    }
    return str.toString();
  }

  public static String randomNumeric(int length) {
    StringBuilder str = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      str.append(NUMERIC.charAt(random.nextInt(NUMERIC_LENGTH)));
    }
    return str.toString();
  }

  public static String randomAlphaNumeric(int length) {
    StringBuilder str = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      str.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC_LENGTH)));
    }
    return str.toString();
  }
}
