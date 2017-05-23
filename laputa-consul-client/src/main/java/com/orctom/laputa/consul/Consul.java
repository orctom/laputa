package com.orctom.laputa.consul;

public class Consul {

  private String host;
  private int port;

  private Services services;
  private KeyValues keyValues;

  public Consul(String host, int port) {
    this.host = host;
    this.port = port;

    services = new Services(this);
    keyValues = new KeyValues(this);
  }

  public Services services() {
    return services;
  }

  public KeyValues keyValues() {
    return keyValues;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }
}
