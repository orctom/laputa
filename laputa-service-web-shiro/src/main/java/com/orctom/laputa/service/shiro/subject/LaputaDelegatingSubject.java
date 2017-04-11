package com.orctom.laputa.service.shiro.subject;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.shiro.session.DefaultLaputaSessionContext;
import com.orctom.laputa.service.shiro.session.LaputaSessionContext;
import com.orctom.laputa.service.shiro.util.RequestPairSource;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.apache.shiro.util.StringUtils;

public class LaputaDelegatingSubject extends DelegatingSubject implements LaputaSubject, RequestPairSource {

  private RequestWrapper requestWrapper;
  private ResponseWrapper responseWrapper;

  public LaputaDelegatingSubject(PrincipalCollection principals,
                                 boolean authenticated,
                                 String host,
                                 Session session,
                                 boolean sessionCreationEnabled,
                                 RequestWrapper requestWrapper,
                                 ResponseWrapper responseWrapper,
                                 SecurityManager securityManager) {
    super(principals, authenticated, host, session, sessionCreationEnabled, securityManager);
    this.requestWrapper = requestWrapper;
    this.responseWrapper = responseWrapper;
  }

  public RequestWrapper getRequestWrapper() {
    return requestWrapper;
  }

  public ResponseWrapper getResponseWrapper() {
    return responseWrapper;
  }

  @Override
  protected boolean isSessionCreationEnabled() {
    return true;
  }

  @Override
  protected SessionContext createSessionContext() {
    LaputaSessionContext wsc = new DefaultLaputaSessionContext();
    String host = getHost();
    if (StringUtils.hasText(host)) {
      wsc.setHost(host);
    }
    wsc.setRequestWrapper(this.requestWrapper);
    wsc.setResponseWrapper(this.responseWrapper);
    return wsc;
  }
}
