package com.orctom.laputa.consul;

import org.junit.Test;

public class ConsulTest {

  @Test
  public void test() {
    Consul consul = new Consul("localhost", 8500);
    consul.keyValues().put("key", "value");
  }
}
