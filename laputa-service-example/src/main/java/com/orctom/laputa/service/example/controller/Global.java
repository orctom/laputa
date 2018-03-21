package com.orctom.laputa.service.example.controller;

import com.orctom.laputa.service.annotation.PATH;
import org.springframework.stereotype.Controller;

@Controller
public class Global {

  @PATH("/500")
  public String error() {
    return "server error";
  }

  @PATH("/")
  public String index() {
    return "hello index";
  }

  @PATH("/login")
  public String login() {
    return "hello login";
  }
}
