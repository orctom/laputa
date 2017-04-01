package com.orctom.laputa.service.shiro.filter;

public class RolesAuthorizationFilter extends AuthorizationFilter {

  @Override
  public String getName() {
    return null;
  }

  @Override
  protected boolean isAccessAllowed() {
    return false;
  }
}
