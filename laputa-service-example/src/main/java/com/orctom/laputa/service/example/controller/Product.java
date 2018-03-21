package com.orctom.laputa.service.example.controller;

import com.orctom.laputa.service.annotation.Data;
import com.orctom.laputa.service.annotation.DefaultValue;
import com.orctom.laputa.service.annotation.GET;
import com.orctom.laputa.service.annotation.PATH;
import com.orctom.laputa.service.annotation.POST;
import com.orctom.laputa.service.annotation.Param;
import com.orctom.laputa.service.annotation.RedirectTo;
import com.orctom.laputa.service.example.model.Products;
import com.orctom.laputa.service.example.model.SKU;
import com.orctom.laputa.service.model.Messenger;
import com.orctom.laputa.service.model.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.validation.constraints.Size;

@Controller
@PATH("/product")
public class Product {

  private static final Logger LOGGER = LoggerFactory.getLogger(Product.class);

  //  @PATH("/hello")
  public String hello() {
    return "hello";
  }

  @PATH("/hello")
  public String hello2(@Param("hello") String hello,
                       @Param("id") @DefaultValue("12345") String id) {
    return "hello: " + hello + ", id=" + id;
  }

  @RedirectTo("/product/sku")
  @PATH("/test")
  public void redirection() {
  }

  @PATH("/test2/{id}")
  public SKU getSku(@Param("id") String id, Messenger messenger) {
    if ("test".equals(id)) {
      messenger.setRedirectTo("/product/sku");
      return null;
    }

    return sku(id);
  }

  @PATH("/sku")
  public SKU sku() {
    return new SKU("315515", "folder", 123456, 100000);
  }

  @PATH("/sku/{id}")
  public SKU sku(@Param("id") String id) {
    return new SKU(id, id + "-desc", 123456, 100000);
  }

  // http://localhost:7000/product/sku/new?sku=sku&desc=desc&category=cate&stock=100
  // curl -X POST -d "sku=sku&desc=desc&category=cate&stock=100" ttp://localhost:7000/product/sku/new
  @PATH("/sku/new")
  @GET
  @POST
  public SKU addSKU(@Param("sku") SKU sku) {
    return sku;
  }

  @PATH("/products")
  @POST
  public String addProducts(@Param("products") Products products) {
    System.out.println(products);
    return "success.";
  }

  // curl -H "Content-Type: application/json" -X POST -d '{"uid":"1001","skus":[{"sku":"123","desc":"a sku","category":"1","stock":"1000"},{"sku":"122","desc":"b sku","category":"2","stock":"100"}]}' http://localhost:7000/product/products/2
  @PATH("/products/2")
  @POST
  public String addProducts2(@Data Products products) {
    System.out.println(products);
    return "success..";
  }

  @PATH("/bytes")
  @POST
  public String bytes(@Data byte[] bytes) {
    System.out.println(bytes.length);
    return "success..";
  }

  // curl -F "file=@sent.txt" http://localhost:7000/product/upload | less
  @POST
  @PATH("/upload")
  public String upload(@Param("file") MultipartFile uploadedFile) {
    LOGGER.debug(uploadedFile.toString());
    return "uploaded: " + uploadedFile.toString();
  }

  @PATH("/hello/{name}")
  public String hello(@Size(min = 3, max = 5) @Param("name") String name) {
    return "hello " + name;
  }

  @PATH("/hello/{name}/a")
  public String helloA(@Param("name") String name) {
    return "hello " + name;
  }

  @PATH("/hello/{name}/b")
  public String helloB(@Param("name") String name) {
    return "hello " + name;
  }

  @PATH("/hello/{name}/attribute/{attribute}")
  public String helloAttribute(@Param("name") String name,
                               @Param("attribute") String attribute) {
    return "hello: " + name + ", attribute: " + attribute;
  }
}
