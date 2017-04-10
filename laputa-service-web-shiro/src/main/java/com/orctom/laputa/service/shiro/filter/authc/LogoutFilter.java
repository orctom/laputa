package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.filter.AbstractFilter;
import com.orctom.laputa.service.shiro.filter.FilterChain;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutFilter extends AbstractFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogoutFilter.class);

  private String redirectUrl = "/";

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

  @Override
  protected void doFilter(RequestWrapper requestWrapper, Context ctx, FilterChain filterChain) {
    Subject subject = getSubject(requestWrapper);
    String redirectUrl = getRedirectUrl();
    try {
      subject.logout();
    } catch (SessionException ise) {
      LOGGER.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
    }
    issueRedirect(requestWrapper, ctx, redirectUrl);
  }

  protected Subject getSubject(RequestWrapper requestWrapper) {
    return SecurityUtils.getSubject();
  }

  protected void issueRedirect(RequestWrapper requestWrapper, Context ctx, String redirectUrl) {
    ctx.setRedirectTo(redirectUrl);
  }
}
