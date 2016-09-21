package com.orctom.laputa.server.test;

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

  @Path(("/search"))
  public String search(String query) {
    return "searching for: " + query;
  }
}
