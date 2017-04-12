package com.orctom.laputa.service.shiro.filter.authz;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.shiro.filter.AccessControlFilter;
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
  protected boolean onAccessDenied(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    Subject subject = SecurityUtils.getSubject();
    // If the subject isn't identified, redirect to login URL
    if (subject.getPrincipal() == null) {
      saveRequestAndRedirectToLogin(requestWrapper, responseWrapper);
    } else {
      responseWrapper.setRedirectTo(getUnauthorizedUrl());
    }
    return false;
  }
}
