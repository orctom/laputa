package com.orctom.laputa.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple counter ¯\_(ツ)_/¯
 * Created by chenhao on 11/30/16.
 */
public class SinpleCounter {

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

  public Map<String, MutableInt> getCounters() {
    return counters;
  }

  public void reset() {
    counters = new HashMap<>();
  }
}
