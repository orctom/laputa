package com.orctom.laputa.service.shiro.lifecycle;

import com.orctom.laputa.service.lifecycle.PostStart;
import com.orctom.laputa.service.shiro.env.LaputaEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiroLoader implements PostStart {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShiroLoader.class);

  @Override
  public void run() {
    LaputaEnvironment environment = new LaputaEnvironment();
    environment.init();
  }

}
