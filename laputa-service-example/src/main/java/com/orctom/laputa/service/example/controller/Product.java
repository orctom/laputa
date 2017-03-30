package com.orctom.laputa.service.example.controller;

import com.orctom.laputa.service.annotation.Data;
import com.orctom.laputa.service.annotation.DefaultValue;
import com.orctom.laputa.service.annotation.GET;
import com.orctom.laputa.service.annotation.POST;
import com.orctom.laputa.service.annotation.Param;
import com.orctom.laputa.service.annotation.Path;
import com.orctom.laputa.service.annotation.RedirectTo;
import com.orctom.laputa.service.example.model.Products;
import com.orctom.laputa.service.example.model.SKU;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.MultipartFile;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
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
      ctx.setRedirectTo("/product/sku");
      return null;
    }

    return sku(id);
  }

  @Path("/sku")
  public SKU sku() {
    try {
      Subject currentUser = SecurityUtils.getSubject();

      if (!currentUser.isAuthenticated()) {
        UsernamePasswordToken token = new UsernamePasswordToken("admin", "oeKoJ49tFQjLwE3D");
        token.setRememberMe(true);
        try {
          System.out.println("login......................");
          currentUser.login(token);
        } catch (UnknownAccountException uae) {
          System.out.println("There is no user with username of " + token.getPrincipal());
        } catch (IncorrectCredentialsException ice) {
          System.out.println("Password for account " + token.getPrincipal() + " was incorrect!");
        } catch (LockedAccountException lae) {
          System.out.println("The account for username " + token.getPrincipal() + " is locked.  " +
              "Please contact your administrator to unlock it.");
        } catch (Exception ae) {
          ae.printStackTrace();
        }
      }

      System.out.println("User [" + currentUser.getPrincipal() + "] logged in successfully.");

      //test a role:
      if (currentUser.hasRole("admin")) {
        System.out.println("Hello admin!");
      }

      //all done - log out!
//      currentUser.logout();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new SKU("315515", "folder", 123456, 100000);
  }

  @Path("/sku/{id}")
  public SKU sku(@Param("id") String id) {
    return new SKU(id, id + "-desc", 123456, 100000);
  }

  // http://localhost:7000/product/sku/new?sku=sku&desc=desc&category=cate&stock=100
  // curl -X POST -d "sku=sku&desc=desc&category=cate&stock=100" ttp://localhost:7000/product/sku/new
  @Path("/sku/new")
  @GET
  @POST
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
