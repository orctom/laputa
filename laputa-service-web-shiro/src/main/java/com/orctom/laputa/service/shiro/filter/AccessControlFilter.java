package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.filter.FilterChain;
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

  protected boolean isLoginRequest(RequestWrapper requestWrapper) {
    return pathsMatch(getLoginUrl(), requestWrapper.getPath());
  }

  protected abstract boolean isAccessAllowed(RequestWrapper requestWrapper,
                                             ResponseWrapper responseWrapper,
                                             Object mappedValue);

  protected abstract void checkAccess(RequestWrapper requestWrapper,
                                      ResponseWrapper responseWrapper,
                                      Object mappedValue,
                                      FilterChain filterChain);

  @Override
  public void onFilterInternal(RequestWrapper requestWrapper,
                               ResponseWrapper responseWrapper,
                               Object mappedValue,
                               FilterChain filterChain) {
    if (isAccessAllowed(requestWrapper, responseWrapper, mappedValue)) {
      return;
    }

    checkAccess(requestWrapper, responseWrapper, mappedValue, filterChain);
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
