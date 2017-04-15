package com.orctom.laputa.config.model;

import java.util.List;
import java.util.Objects;

public class Service {

  private String node;
  private String nodeAddress;
  private String serviceAddress;
  private String serviceId;
  private String serviceName;
  private List<String> serviceTags;
  private int servicePort;

  public Service(String node,
                 String nodeAddress,
                 String serviceAddress,
                 String serviceId,
                 String serviceName,
                 List<String> serviceTags,
                 int servicePort) {
    this.node = node;
    this.nodeAddress = nodeAddress;
    this.serviceAddress = serviceAddress;
    this.serviceId = serviceId;
    this.serviceName = serviceName;
    this.serviceTags = serviceTags;
    this.servicePort = servicePort;
  }

  public String getNode() {
    return node;
  }

  public String getNodeAddress() {
    return nodeAddress;
  }

  public String getServiceAddress() {
    return serviceAddress;
  }

  public String getServiceId() {
    return serviceId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public List<String> getServiceTags() {
    return serviceTags;
  }

  public int getServicePort() {
    return servicePort;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Service that = (Service) o;

    return Objects.equals(node, that.node)
        && Objects.equals(nodeAddress, that.nodeAddress)
        && Objects.equals(serviceId, that.serviceId)
        && Objects.equals(serviceName, that.serviceName)
        && Objects.equals(serviceTags, that.serviceTags)
        && Objects.equals(serviceAddress, that.serviceAddress)
        && Objects.equals(servicePort, that.servicePort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(node, nodeAddress, serviceId, serviceName, serviceTags, serviceAddress, servicePort);
  }
}
