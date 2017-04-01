package com.orctom.laputa.service.shiro.filter;

public class PermissionsAuthorizationFilter extends AuthenticationFilter {

  @Override
  public String getName() {
    return null;
  }

  @Override
  protected boolean isAccessAllowed() {
    return false;
  }
}
