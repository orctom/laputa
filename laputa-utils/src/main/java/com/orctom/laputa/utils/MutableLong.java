package com.orctom.laputa.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Mutable Integer
 * Created by hao on 8/7/16.
 */
public class MutableLong implements Comparable<MutableLong> {

  private AtomicLong value;

  public MutableLong(int value) {
    this.value = new AtomicLong(value);
  }

  public long getAndSet(int newValue) {
    return value.getAndSet(newValue);
  }

  public long getValue() {
    return value.get();
  }

  public void setValue(long value) {
    this.value.set(value);
  }

  public long increase() {
    return value.incrementAndGet();
  }

  public long increaseBy(int delta) {
    return value.addAndGet(delta);
  }

  @Override
  public int compareTo(MutableLong o) {
    return Long.compare(o.getValue(), this.getValue());
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
