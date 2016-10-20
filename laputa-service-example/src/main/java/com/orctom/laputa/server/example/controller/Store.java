package com.orctom.laputa.server.example.controller;

import com.orctom.laputa.server.annotation.GET;
import com.orctom.laputa.server.annotation.POST;
import com.orctom.laputa.server.annotation.Path;
import org.springframework.stereotype.Controller;

@Controller
@Path("/store")
public class Store {

  @Path("/{name}")
  public String name() {
    return "name";
  }

  @Path("/{id}")
  public String id(String id) {
    return "hello " + id;
  }

  @POST @GET
  @Path("/search")
  public String search(String query) {
    return "searching for: " + query;
  }
}
