package com.orctom.laputa.consul.model;

public class Check {

  private String id;
  private String name;
  private String script;
  private String http;
  private String tcp;
  private String ttl;
  private String interval;
  private String timeout;
  private String dockerContainerId;
  private String shell = "/bin/bash";
  private String notes;
  private String deregisterCriticalServiceAfter;
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  public String getHttp() {
    return http;
  }

  public void setHttp(String http) {
    this.http = http;
  }

  public String getTcp() {
    return tcp;
  }

  public void setTcp(String tcp) {
    this.tcp = tcp;
  }

  public String getTtl() {
    return ttl;
  }

  public void setTtl(String ttl) {
    this.ttl = ttl;
  }

  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public String getTimeout() {
    return timeout;
  }

  public void setTimeout(String timeout) {
    this.timeout = timeout;
  }

  public String getDockerContainerId() {
    return dockerContainerId;
  }

  public void setDockerContainerId(String dockerContainerId) {
    this.dockerContainerId = dockerContainerId;
  }

  public String getShell() {
    return shell;
  }

  public void setShell(String shell) {
    this.shell = shell;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getDeregisterCriticalServiceAfter() {
    return deregisterCriticalServiceAfter;
  }

  public void setDeregisterCriticalServiceAfter(String deregisterCriticalServiceAfter) {
    this.deregisterCriticalServiceAfter = deregisterCriticalServiceAfter;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
