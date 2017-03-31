package com.orctom.laputa.service.shiro.processor;

import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.SecurityConfig;
import com.orctom.laputa.service.processor.PreProcessor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Authenticator implements PreProcessor {

  private static final SecurityConfig SECURITY_CONFIG = Configurator.getInstance().getSecurityConfig();

  public Authenticator() {
    System.out.println("init Authenticator.........................");
  }

  @PostConstruct
  public void init() {
    System.out.println("inited.....");
  }

  @Override
  public int getOrder() {
    return 0;
  }

  @Override
  public void process(RequestWrapper requestWrapper, Context ctx) {
    System.out.println(".....................");
    if (null == SECURITY_CONFIG) {
      return;
    }

    if (isRequestingNonProtectedResource(requestWrapper.getPath())) {
      return;
    }

    Subject currentUser = SecurityUtils.getSubject();
    if (!currentUser.isAuthenticated()) {
      ctx.setRedirectTo("/login.html");
    }
  }

  private boolean isRequestingNonProtectedResource(String path) {
    // TODO
    return false;
  }
}
