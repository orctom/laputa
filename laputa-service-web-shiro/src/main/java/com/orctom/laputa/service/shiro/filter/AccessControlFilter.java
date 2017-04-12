package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public abstract class AccessControlFilter extends PathMatchingFilter {

  protected static final String KEY_REDIRECT_URL = "_redirect_";
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

  protected abstract boolean isAccessAllowed(RequestWrapper requestWrapper,
                                             ResponseWrapper responseWrapper,
                                             Object mappedValue);

  private boolean onAccessDenied(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, Object mappedValue) {
    return onAccessDenied(requestWrapper, responseWrapper);
  }

  protected abstract boolean onAccessDenied(RequestWrapper requestWrapper, ResponseWrapper responseWrapper);

  @Override
  public boolean onPreHandle(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, Object mappedValue) {
    return isAccessAllowed(requestWrapper, responseWrapper, mappedValue) ||
        onAccessDenied(requestWrapper, responseWrapper, mappedValue);
  }

  protected boolean isLoginRequest(RequestWrapper requestWrapper) {
    return pathsMatch(getLoginUrl(), requestWrapper.getPath());
  }

  protected void saveRequestAndRedirectToLogin(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    saveRequest(requestWrapper);
    redirectToLogin(requestWrapper, responseWrapper);
  }

  protected void saveRequest(RequestWrapper requestWrapper) {
    Subject subject = SecurityUtils.getSubject();
    Session session = subject.getSession();
    session.setAttribute(KEY_REDIRECT_URL, requestWrapper.getUri());
  }

  protected void redirectToLogin(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    responseWrapper.setRedirectTo(getLoginUrl());
  }
}
