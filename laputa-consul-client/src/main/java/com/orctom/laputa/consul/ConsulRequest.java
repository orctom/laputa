package com.orctom.laputa.consul;

public abstract class ConsulRequest {

  private Consul consul;

  ConsulRequest(Consul consul) {
    this.consul = consul;
  }

  public void execute() {
//    ConsulRequestExecutor.submit(() -> {
//
//    });
  }

  Consul getConsul() {
    return consul;
  }
}
