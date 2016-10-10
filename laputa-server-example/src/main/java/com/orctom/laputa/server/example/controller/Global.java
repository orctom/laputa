package com.orctom.laputa.server.example.controller;

import com.orctom.laputa.server.annotation.Path;
import org.springframework.stereotype.Controller;

@Controller
public class Global {

  @Path("/500")
  public String error() {
    return "server error";
  }
}
