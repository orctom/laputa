package com.orctom.laputa.service.shiro.filter;

public class FormAuthenticationFilter extends AuthenticationFilter {

  @Override
  public String getName() {
    return null;
  }

  @Override
  protected boolean isAccessAllowed() {
    return false;
  }
}
