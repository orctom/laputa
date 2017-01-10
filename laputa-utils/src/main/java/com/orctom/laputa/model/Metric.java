package com.orctom.laputa.model;

public class Metric {

  private String key;
  private int value;
  private Float rate;
  private String gauge;

  public Metric() {
  }

  public Metric(Metric metric) {
    this.key = metric.getKey();
    this.value = metric.getValue();
    this.rate = metric.getRate();
    this.gauge = metric.getGauge();
  }

  public Metric(String key, String gauge) {
    this.key = key;
    this.gauge = gauge;
  }

  public Metric(String key, int value, Float rate) {
    this.key = key;
    this.value = value;
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

  public String getGauge() {
    return gauge;
  }

  public void setGauge(String gauge) {
    this.gauge = gauge;
  }

  @Override
  public String toString() {
    if (null == gauge) {
      return "Metric{" +
          "key='" + key + '\'' +
          ", value=" + value +
          ", rate=" + rate +
          '}';
    } else {
      return "Metric{" +
          "key='" + key + '\'' +
          ", gauge='" + gauge + '\'' +
          '}';
    }
  }
}
