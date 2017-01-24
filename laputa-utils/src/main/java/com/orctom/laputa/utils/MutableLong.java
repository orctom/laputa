package com.orctom.laputa.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Mutable Integer
 * Created by hao on 8/7/16.
 */
public class MutableLong implements Comparable<MutableLong> {

  private AtomicLong value;

  public MutableLong(long value) {
    this.value = new AtomicLong(value);
  }

  public long getAndSet(long newValue) {
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

  public long increaseBy(long delta) {
    return value.addAndGet(delta);
  }

  public long decrease() {
    return value.decrementAndGet();
  }

  public long decreaseBy(long delta) {
    return increaseBy(-delta);
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
