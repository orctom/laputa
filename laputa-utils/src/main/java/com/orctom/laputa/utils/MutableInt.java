package com.orctom.laputa.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mutable Integer
 * Created by hao on 8/7/16.
 */
public class MutableInt implements Comparable<MutableInt> {

  private AtomicInteger value;

  public MutableInt(int value) {
    this.value = new AtomicInteger(value);
  }

  public int getAndSet(int newValue) {
    return value.getAndSet(newValue);
  }

  public int getValue() {
    return value.get();
  }

  public void setValue(int value) {
    this.value.set(value);
  }

  public int increase() {
    return value.incrementAndGet();
  }

  public int increaseBy(int delta) {
    return value.addAndGet(delta);
  }

  public int decrease() {
    return value.decrementAndGet();
  }

  public int decreaseBy(int delta) {
    return increaseBy(-delta);
  }

  @Override
  public int compareTo(MutableInt o) {
    return Integer.compare(o.getValue(), this.getValue());
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
