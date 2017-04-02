package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import org.apache.shiro.subject.Subject;

public abstract class AuthenticationFilter extends AccessControlFilter {

  private String successUrl = "/";

  public String getSuccessUrl() {
    return successUrl;
  }

  public void setSuccessUrl(String successUrl) {
    this.successUrl = successUrl;
  }

  protected boolean isAccessAllowed(RequestWrapper requestWrapper, Context context, Object mappedValue) {
    Subject subject = getSubject(requestWrapper);
    return subject.isAuthenticated();
  }

  protected void issueSuccessRedirect(RequestWrapper requestWrapper, Context context) {
    super.setRedirect(context, getSuccessUrl());
  }
}
