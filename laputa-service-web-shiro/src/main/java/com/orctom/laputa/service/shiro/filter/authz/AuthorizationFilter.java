package com.orctom.laputa.service.shiro.filter.authz;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.filter.AccessControlFilter;
import com.orctom.laputa.service.shiro.filter.FilterChain;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public abstract class AuthorizationFilter extends AccessControlFilter {

  private String unauthorizedUrl = "/403.html";

  public String getUnauthorizedUrl() {
    return unauthorizedUrl;
  }

  public void setUnauthorizedUrl(String unauthorizedUrl) {
    this.unauthorizedUrl = unauthorizedUrl;
  }

  @Override
  protected void checkAccess(RequestWrapper requestWrapper,
                             Context context,
                             Object mappedValue,
                             FilterChain filterChain) {
    Subject subject = SecurityUtils.getSubject();
    // If the subject isn't identified, redirect to login URL
    if (subject.getPrincipal() == null) {
      saveRequestAndRedirectToLogin(requestWrapper, context);
    } else {
      context.setRedirectTo(getUnauthorizedUrl());
    }
  }
}
