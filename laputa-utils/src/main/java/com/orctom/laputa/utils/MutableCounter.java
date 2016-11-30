package com.orctom.laputa.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mutable Integer
 * Created by hao on 8/7/16.
 */
public class MutableCounter {

  private AtomicInteger value;

  public MutableCounter(int value) {
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
}
