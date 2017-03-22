package com.orctom.laputa.client.service;

import com.orctom.laputa.client.Laputa;

public class DummyServiceImpl implements DummyService {

  public static void main(String[] args) throws Exception {
    DummyService service = Laputa.lookup(DummyService.class);
    System.out.println("==================");
    System.out.println("<<" + service.hello("baby") + ">>");
    System.out.println("==================");
  }

  @Override
  public String hello(String name) {
    return name;
  }
}
