package com.orctom.laputa.service.shiro.subject;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
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
  private Context context;

  public LaputaDelegatingSubject(PrincipalCollection principals,
                                 boolean authenticated,
                                 String host,
                                 Session session,
                                 boolean sessionCreationEnabled,
                                 RequestWrapper requestWrapper,
                                 Context context,
                                 SecurityManager securityManager) {
    super(principals, authenticated, host, session, sessionCreationEnabled, securityManager);
    this.requestWrapper = requestWrapper;
    this.context = context;
  }

  public RequestWrapper getRequestWrapper() {
    return requestWrapper;
  }

  public Context getContext() {
    return context;
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
    wsc.setContext(this.context);
    return wsc;
  }
}
