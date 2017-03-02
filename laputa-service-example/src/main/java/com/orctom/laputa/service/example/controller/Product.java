package com.orctom.laputa.service.example.controller;

import com.orctom.laputa.service.annotation.*;
import com.orctom.laputa.service.example.model.SKU;
import com.orctom.laputa.service.example.model.Products;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.validation.constraints.Size;

@Controller
@Path("/product")
public class Product {

  private static final Logger LOGGER = LoggerFactory.getLogger(Product.class);

  //  @Path("/hello")
  public String hello() {
    return "hello";
  }

  @Path("/hello")
  public String hello2(@Param("hello") String hello,
                       @Param("id") @DefaultValue("12345") String id) {
    return "hello: " + hello + ", id=" + id;
  }

  @RedirectTo("/product/sku")
  @Path("/test")
  public void redirection() {
  }

  @Path("/test2/{id}")
  public SKU getSku(@Param("id") String id, Context ctx) {
    if ("test".equals(id)) {
      ctx.redirectTo("/product/sku");
      return null;
    }

    return sku(id);
  }

  @Path("/sku")
  public SKU sku() {
    return new SKU("315515", "folder", 123456, 100000);
  }

  @Path("/sku/{id}")
  public SKU sku(@Param("id") String id) {
    return new SKU(id, id + "-desc", 123456, 100000);
  }

  // http://localhost:7000/product/sku/new?sku=sku&desc=desc&category=cate&stock=100
  @Path("/sku/new")
  @GET @POST
  public SKU addSKU(@Param("sku") SKU sku) {
    return sku;
  }

  @Path("/products")
  @POST
  public String addProducts(@Param("products") Products products) {
    System.out.println(products);
    return "success.";
  }

  // curl -H "Content-Type: application/json" -X POST -d '{"uid":"1001","skus":[{"sku":"123","desc":"a sku","category":"1","stock":"1000"},{"sku":"122","desc":"b sku","category":"2","stock":"100"}]}' http://localhost:7000/product/products/2
  @Path("/products/2")
  @POST
  public String addProducts2(@Data Products products) {
    System.out.println(products);
    return "success..";
  }

  // curl -F "file=@sent.txt" http://localhost:7000/product/upload | less
  @POST
  @Path("/upload")
  public String upload(@Param("file") MultipartFile uploadedFile) {
    LOGGER.debug(uploadedFile.toString());
    return "uploaded: " + uploadedFile.toString();
  }

  @Path("/hello/{name}")
  public String hello(@Size(min = 3, max = 5) @Param("name") String name) {
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
