package com.orctom.laputa.utils;

import java.util.Random;

public abstract class RandomUtils {

  private static final String ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
  private static final String NUMERIC = "1234567890";

  private static final int ALPHA_LENGTH = ALPHA.length();
  private static final int ALPHANUMERIC_LENGTH = ALPHANUMERIC.length();
  private static final int NUMERIC_LENGTH = NUMERIC.length();

  private static final Random RANDOM = new Random();

  public static String randomAlpha(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("Invalid length: " + length);
    }
    StringBuilder str = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      str.append(ALPHA.charAt(RANDOM.nextInt(ALPHA_LENGTH)));
    }
    return str.toString();
  }

  public static String randomNumeric(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("Invalid length: " + length);
    }
    StringBuilder str = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      str.append(NUMERIC.charAt(RANDOM.nextInt(NUMERIC_LENGTH)));
    }
    return str.toString();
  }

  public static String randomAlphaNumeric(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("Invalid length: " + length);
    }
    StringBuilder str = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      str.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC_LENGTH)));
    }
    return str.toString();
  }

  public static long nextLong(final long start, final long end) {
    return (long) nextDouble(start, end);
  }

  public static double nextDouble(final double start, final double end) {
    if (start < 0) {
      throw new IllegalArgumentException("Invalid start: " + start);
    }
    if (start > end) {
      throw new IllegalArgumentException("Invalid end: " + end + ", which bigger than start");
    }
    if (start == end) {
      return start;
    }

    return start + ((end - start) * RANDOM.nextDouble());
  }
}
