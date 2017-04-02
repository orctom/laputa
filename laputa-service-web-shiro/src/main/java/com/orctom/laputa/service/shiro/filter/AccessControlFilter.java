package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public abstract class AccessControlFilter extends PathMatchingFilter {

  private String loginUrl = "/login.html";

  public String getLoginUrl() {
    return loginUrl;
  }

  public void setLoginUrl(String loginUrl) {
    this.loginUrl = loginUrl;
  }

  protected Subject getSubject(RequestWrapper requestWrapper) {
    return SecurityUtils.getSubject();
  }

  protected boolean isLoginRequest(RequestWrapper requestWrapper) {
    return pathsMatch(getLoginUrl(), requestWrapper.getPath());
  }

  protected abstract boolean isAccessAllowed(RequestWrapper requestWrapper, Context context, Object mappedValue);

  protected abstract boolean onAccessDenied(RequestWrapper requestWrapper, Context context, Object mappedValue);

  public boolean onPreHandle(RequestWrapper requestWrapper, Context context, Object mappedValue) {
    return isAccessAllowed(requestWrapper, context, mappedValue) || onAccessDenied(requestWrapper, context, mappedValue);
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
