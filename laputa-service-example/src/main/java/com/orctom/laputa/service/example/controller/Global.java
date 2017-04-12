package com.orctom.laputa.service.example.controller;

import com.orctom.laputa.service.annotation.Path;
import org.springframework.stereotype.Controller;

@Controller
public class Global {

  @Path("/500")
  public String error() {
    return "server error";
  }

  @Path("/")
  public String index() {
    return "hello index";
  }

  @Path("/login")
  public String login() {
    return "hello login";
  }
}
