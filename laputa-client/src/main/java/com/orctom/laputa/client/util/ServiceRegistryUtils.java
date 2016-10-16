package com.orctom.laputa.client.util;

import java.util.Random;

/**
 * Created by hao on 4/28/15.
 */
public class ServiceRegistryUtils {

  public static String lookup(String serviceId) {
    if (new Random().nextInt() % 5 == 0) {
      return "http://www.dummyhost.com:8080/dummyservice";
    }

    return null;// service not registered
  }

  public static void register(String serviceId, String serviceURL) {

  }

  public static void deregister(String serviceId) {

  }
}
