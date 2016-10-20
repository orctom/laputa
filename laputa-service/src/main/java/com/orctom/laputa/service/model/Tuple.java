package com.orctom.laputa.service.model;

/**
 * tuple bean
 * Created by hao on 11/30/15.
 */
public class Tuple<K, V> {

  private K key;
  private V value;

  public Tuple(K key, V value) {

    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }
}
