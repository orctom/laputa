package com.orctom.laputa.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * A simple counter ¯\_(ツ)_/¯
 * Created by chenhao on 11/30/16.
 */
public class SimpleCounter {

  private Map<String, LongAdder> counters = new HashMap<>();

  public void count(String key) {
    getCounter(key).increment();
  }

  public void count(String key, int increment) {
    getCounter(key).add(increment);
  }

  private LongAdder getCounter(String key) {
    LongAdder counter = counters.get(key);
    if (null != counter) {
      return counter;
    }

    counter = new LongAdder();
    synchronized (this) {
      LongAdder old = counters.put(key, counter);
      if (null != old) {
        counter.add(old.longValue());
      }
    }
    return counter;
  }

  public List<Map.Entry<String, LongAdder>> getResult() {
    List<Map.Entry<String, LongAdder>> result = counters.entrySet().stream().collect(Collectors.toList());
    result.sort(Comparator.comparingLong(o -> o.getValue().longValue()));
    return result;
  }

  public void reset() {
    counters = new HashMap<>();
  }
}
