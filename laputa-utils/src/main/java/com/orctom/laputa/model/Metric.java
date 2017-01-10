package com.orctom.laputa.model;

public class Metric {

  private String key;
  private int value;
  private Float rate;

  public Metric() {
  }

  public Metric(Metric metric) {
    this.key = metric.getKey();
    this.value = metric.getValue();
    if (null != metric.getRate()) {
      this.rate = metric.getRate();
    }
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

  public Float getRate() {
    return rate;
  }

  public void setRate(Float rate) {
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
