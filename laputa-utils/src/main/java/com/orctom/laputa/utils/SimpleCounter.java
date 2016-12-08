package com.orctom.laputa.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple counter ¯\_(ツ)_/¯
 * Created by chenhao on 11/30/16.
 */
public class SimpleCounter {

  private Map<String, MutableInt> counters = new HashMap<>();

  public int count(String key) {
    return getCounter(key).increase();
  }

  public int count(String key, int increment) {
    return getCounter(key).increaseBy(increment);
  }

  private MutableInt getCounter(String key) {
    MutableInt counter = counters.get(key);
    if (null != counter) {
      return counter;
    }

    counter = new MutableInt(0);
    synchronized (this) {
      MutableInt old = counters.put(key, counter);
      if (null != old) {
        counter.increaseBy(old.getValue());
      }
    }
    return counter;
  }

  public List<Map.Entry<String, MutableInt>> getResult() {
    List<Map.Entry<String, MutableInt>> result = counters.entrySet().stream().collect(Collectors.toList());
    Collections.sort(result, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
    return result;
  }

  public void reset() {
    counters = new HashMap<>();
  }
}
