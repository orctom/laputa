package com.orctom.laputa.server.test;

import com.orctom.laputa.server.annotation.*;
import com.orctom.laputa.server.test.model.SKU;

@Export
@Path("/product")
public class Product {

//  @Path("/hello")
  public String hello() {
    return "hello";
  }

  @Path("/hello")
  public String hello2(@Param("hello") String hello,
                       @Param("id") @DefaultValue("12345") String id) {
    return "hello: " + hello + ", id=" + id;
  }

  @Path("/sku")
  public SKU sku() {
    return new SKU("315515", "folder", 123456, 100000);
  }

  @Path("/hello/{name}")
  public String hello(@Param("name") String name) {
    return "hello " + name;
  }

  @Path("/hello/{name}/a")
  public String helloA(@Param("name") String name) {
    return "hello " + name;
  }

  @Path("/hello/{name}/b")
  public String helloB(@Param("name") String name) {
    return "hello " + name;
  }

  @Path("/hello/{name}/attribute/{attribute}")
  public String helloAttribute(@Param("name") String name,
                               @Param("attribute") String attribute) {
    return "hello: " + name + ", attribute: " + attribute;
  }
}
