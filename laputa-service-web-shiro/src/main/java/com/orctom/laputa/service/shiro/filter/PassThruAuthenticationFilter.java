package com.orctom.laputa.service.shiro.filter;

public class PassThruAuthenticationFilter extends AuthenticationFilter {

  @Override
  public String getName() {
    return "passThru";
  }

  @Override
  protected boolean isAccessAllowed() {
    return false;
  }
}
