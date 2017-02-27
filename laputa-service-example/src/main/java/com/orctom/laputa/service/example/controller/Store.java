package com.orctom.laputa.service.example.controller;

import com.orctom.laputa.service.annotation.GET;
import com.orctom.laputa.service.annotation.POST;
import com.orctom.laputa.service.annotation.Param;
import com.orctom.laputa.service.annotation.Path;
import org.springframework.stereotype.Controller;

@Controller
@Path("/store")
public class Store {

  @Path("/{name}")
  public String name() {
    return "name";
  }

  @Path("/{id}")
  public String id(@Param("id") String id) {
    return "hello " + id;
  }

  @POST
  @GET
  @Path("/search")
  public String search(@Param("query") String query) {
    return "searching for: " + query;
  }
}
