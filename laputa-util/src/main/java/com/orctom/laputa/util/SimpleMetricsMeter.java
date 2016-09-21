package com.orctom.laputa.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mutable Integer
 * Created by hao on 8/7/16.
 */
public class SimpleMetricsMeter {

  private AtomicInteger value;

  public SimpleMetricsMeter(int value) {
    this.value = new AtomicInteger(value);
  }

  public void setValue(int value) {
    this.value.set(value);
  }

  public int getAndSet(int newValue) {
    return value.getAndSet(newValue);
  }

  public int getValue() {
    return value.get();
  }

  public void increase() {
    value.incrementAndGet();
  }

  public void increaseBy(int delta) {
    value.addAndGet(delta);
  }
}
