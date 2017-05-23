package com.orctom.laputa.consul;

import com.orctom.laputa.consul.model.Service;

import java.util.List;

public class Services {

  private Consul consul;

  Services(Consul consul) {
    this.consul = consul;
  }

  public void register(Service service) {
  }

  public void deregister(String id) {
    // deregister service


    // deregister check
  }

  public List<Service> lookup(String service) {
    return null;
  }
}
