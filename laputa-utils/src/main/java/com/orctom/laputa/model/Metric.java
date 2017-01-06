package com.orctom.laputa.model;

public class Metric {

  private String key;
  private int value;
  private float rate;

  public Metric() {
  }

  public Metric(String key, int value) {
    this.key = key;
    this.value = value;
  }

  public Metric(String key, int value, float rate) {
    this(key, value);
    this.rate = rate;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public float getRate() {
    return rate;
  }

  public void setRate(float rate) {
    this.rate = rate;
  }

  @Override
  public String toString() {
    return "Metric{" +
        "key='" + key + '\'' +
        ", value=" + value +
        ", rate=" + rate +
        '}';
  }
}
