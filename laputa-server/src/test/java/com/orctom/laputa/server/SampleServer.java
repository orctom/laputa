package com.orctom.laputa.server;

import com.orctom.laputa.server.annotation.Export;

public class SampleServer {

  public static void main(String[] args) {
    try {
      new LaputaService(9000, true)
          .scanPackage("com.orctom.laputa.server.test")
          .forAnnotation(Export.class)
          .startup();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
