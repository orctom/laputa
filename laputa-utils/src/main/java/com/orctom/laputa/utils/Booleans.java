package com.orctom.laputa.utils;

public abstract class Booleans {

  public static boolean isBooleam(String value) {
    return value != null &&
        (value.equalsIgnoreCase("true") ||
            value.equalsIgnoreCase("t") ||
            value.equalsIgnoreCase("1") ||
            value.equalsIgnoreCase("enabled") ||
            value.equalsIgnoreCase("y") ||
            value.equalsIgnoreCase("yes") ||
            value.equalsIgnoreCase("on"));
  }
}
