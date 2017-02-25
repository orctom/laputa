package com.orctom.laputa.service.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * accepts in headers
 * Created by hao on 11/30/15.
 */
public class Accepts {

  private static Map<String, List<String>> cache = new HashMap<>();

  public static List<String> sortAsList(String accept) {
    if (null == accept) {
      return null;
    }

    List<String> sorted = cache.get(accept);
    if (null != sorted) {
      return sorted;
    }

    String[] acceptArray = accept.split(",");
    int len = acceptArray.length;

    List<Tuple<String, String>> accepts = new ArrayList<>(len);

    for (String item : acceptArray) {
      int semiColonIndex = item.indexOf(";");
      if (semiColonIndex > 0) {
        String key = item.substring(0, semiColonIndex);
        String value = item.substring(semiColonIndex + 1);
        accepts.add(new Tuple<>(key, value));
      } else {
        accepts.add(new Tuple<>(item, "q=1.0"));
      }
    }

    accepts.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    sorted = accepts.stream().map(Tuple::getKey).collect(Collectors.toList());
    cache.put(accept, sorted);

    return sorted;
  }
}
