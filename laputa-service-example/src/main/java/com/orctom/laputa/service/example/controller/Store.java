package com.orctom.laputa.service.example.controller;

import com.orctom.laputa.service.annotation.GET;
import com.orctom.laputa.service.annotation.PATH;
import com.orctom.laputa.service.annotation.POST;
import com.orctom.laputa.service.annotation.Param;
import org.springframework.stereotype.Controller;

@Controller
@PATH("/store")
public class Store {

  @PATH("/{name}")
  public String name() {
    return "name";
  }

  @PATH("/{id}")
  public String id(@Param("id") String id) {
    return "hello " + id;
  }

  @POST
  @GET
  @PATH("/search")
  public String search(@Param("query") String query) {
    return "searching for: " + query;
  }
}
