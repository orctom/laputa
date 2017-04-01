package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public abstract class AuthenticationFilter extends PathMatchingFilter {

  private String loginUrl = "/login.html";

  public String getLoginUrl() {
    return loginUrl;
  }

  public void setLoginUrl(String loginUrl) {
    this.loginUrl = loginUrl;
  }

  protected boolean isLoginRequest(RequestWrapper requestWrapper) {
    return pathsMatch(getLoginUrl(), requestWrapper.getPath());
  }

  protected void saveRequestAndRedirectToLogin(RequestWrapper requestWrapper, Context context) {
    saveRequest(requestWrapper);
    redirectToLogin(requestWrapper, context);
  }

  protected void saveRequest(RequestWrapper requestWrapper) {
    Subject subject = SecurityUtils.getSubject();
    Session session = subject.getSession();
    session.setAttribute("_redirect_", requestWrapper.getUri());
  }

  protected void redirectToLogin(RequestWrapper requestWrapper, Context context) {
    context.setRedirectTo(getLoginUrl());
  }
}
