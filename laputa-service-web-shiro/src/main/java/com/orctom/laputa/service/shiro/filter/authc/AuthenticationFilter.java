package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.shiro.filter.AccessControlFilter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public abstract class AuthenticationFilter extends AccessControlFilter {

  private String successUrl = "/";

  public String getSuccessUrl() {
    return successUrl;
  }

  public void setSuccessUrl(String successUrl) {
    this.successUrl = successUrl;
  }

  protected boolean isAccessAllowed(RequestWrapper requestWrapper,
                                    ResponseWrapper responseWrapper,
                                    Object mappedValue) {
    Subject subject = getSubject(requestWrapper);
    return subject.isAuthenticated();
  }

  protected void issueSuccessRedirect(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    String redirectUrl = getSuccessRedirectUrl();
    responseWrapper.setRedirectTo(redirectUrl);
  }

  private String getSuccessRedirectUrl() {
    Subject subject = SecurityUtils.getSubject();
    Session session = subject.getSession();
    Object redirectUrl = session.getAttribute(KEY_REDIRECT_URL);
    if (null == redirectUrl) {
      return getSuccessUrl();
    }

    return (String) redirectUrl;
  }
}
