package com.orctom.laputa.service;

public class DummyModel {

  public DummyModel() {
  }

  public DummyModel(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }

  private String name;
  private String desc;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  @Override
  public String toString() {
    return "<<[name: " + name + ", desc: " + desc + "]>>";
  }

}
