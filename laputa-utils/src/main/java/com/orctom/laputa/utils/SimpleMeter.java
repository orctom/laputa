package com.orctom.laputa.utils;

import java.util.concurrent.atomic.LongAdder;

/**
 * Mutable Integer
 * Created by hao on 8/7/16.
 */
public class SimpleMeter implements Comparable<SimpleMeter> {

  private LongAdder value;

  public SimpleMeter() {
    this.value = new LongAdder();
  }

  public int getValue() {
    return value.intValue();
  }

  public void increase() {
    value.increment();
  }

  public void increaseBy(int delta) {
    value.add(delta);
  }

  public void decrease() {
    value.decrement();
  }

  public void decreaseBy(int delta) {
    value.add(-delta);
  }

  public void reset() {
    value.reset();
  }

  @Override
  public int compareTo(SimpleMeter o) {
    return Integer.compare(o.getValue(), this.getValue());
  }

  @Override
  public String toString() {
    return String.valueOf(value.longValue());
  }
}
