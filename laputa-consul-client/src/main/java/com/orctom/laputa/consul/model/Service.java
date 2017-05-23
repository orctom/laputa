package com.orctom.laputa.consul.model;

import java.util.List;
import java.util.Objects;

public class Service {

  private String name;
  private String id;
  private List<String> tags;
  private String address;
  private int port;
  private List<Check> checks;

  public Service() {
  }

  public Service(String name,
                 String id,
                 List<String> tags,
                 String address,
                 int port) {
    this.name = name;
    this.id = id;
    this.tags = tags;
    this.address = address;
    this.port = port;
  }

  public Service(String name,
                 String id,
                 List<String> tags,
                 String address,
                 int port,
                 List<Check> checks) {
    this.name = name;
    this.id = id;
    this.tags = tags;
    this.address = address;
    this.port = port;
    this.checks = checks;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public List<Check> getChecks() {
    return checks;
  }

  public void setChecks(List<Check> checks) {
    this.checks = checks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Service that = (Service) o;

    return Objects.equals(id, that.id)
        && Objects.equals(name, that.name)
        && Objects.equals(tags, that.tags)
        && Objects.equals(address, that.address)
        && Objects.equals(port, that.port);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, id, tags, address, port);
  }

  public static class ServiceBuilder {

    private String name;
    private String id;
    private List<String> tags;
    private String address;
    private int port;

    public ServiceBuilder id(String id) {
      this.id = id;
      return this;
    }

    public ServiceBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ServiceBuilder tags(List<String> tags) {
      this.tags = tags;
      return this;
    }

    public ServiceBuilder address(String address) {
      this.address = address;
      return this;
    }

    public ServiceBuilder port(int port) {
      this.port = port;
      return this;
    }

    public Service build() {
      return new Service(name, id, tags, address, port);
    }
  }
}
