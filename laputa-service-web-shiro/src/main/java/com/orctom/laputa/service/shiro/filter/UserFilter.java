package com.orctom.laputa.service.shiro.filter;

public class UserFilter extends AccessControlFilter {

  @Override
  public String getName() {
    return null;
  }

  @Override
  protected boolean isAccessAllowed() {
    return false;
  }
}
