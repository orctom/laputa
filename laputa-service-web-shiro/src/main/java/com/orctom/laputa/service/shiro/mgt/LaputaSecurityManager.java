package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.shiro.cookie.CookieRememberMeManager;
import com.orctom.laputa.service.shiro.session.DefaultLaputaSessionContext;
import com.orctom.laputa.service.shiro.session.LaputaSessionKey;
import com.orctom.laputa.service.shiro.session.LaputaSessionManager;
import com.orctom.laputa.service.shiro.session.LaputaSessionStorageEvaluator;
import com.orctom.laputa.service.shiro.subject.LaputaSubjectContext;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.SubjectContext;

import java.io.Serializable;

import static com.orctom.laputa.service.shiro.util.RequestPairSourceUtils.getRequestWrapper;
import static com.orctom.laputa.service.shiro.util.RequestPairSourceUtils.getResponseWrapper;

public class LaputaSecurityManager extends DefaultSecurityManager {

  public LaputaSecurityManager() {
    super();
    ((DefaultSubjectDAO) this.subjectDAO).setSessionStorageEvaluator(new LaputaSessionStorageEvaluator());
    setSubjectFactory(new LaputaSubjectFactory());
    setRememberMeManager(new CookieRememberMeManager());
    setSessionManager(new LaputaSessionManager());
  }

  @Override
  protected SubjectContext createSubjectContext() {
    return new LaputaSubjectContext();
  }

  @Override
  protected SubjectContext copy(SubjectContext subjectContext) {
    if (subjectContext instanceof LaputaSubjectContext) {
      return subjectContext;
    }
    return super.copy(subjectContext);
  }

  @Override
  protected SessionContext createSessionContext(SubjectContext subjectContext) {
    SessionContext sessionContext = super.createSessionContext(subjectContext);
    if (subjectContext instanceof LaputaSubjectContext) {
      LaputaSubjectContext lsc = (LaputaSubjectContext) subjectContext;
      RequestWrapper requestWrapper = lsc.getRequestWrapper();
      ResponseWrapper responseWrapper = lsc.getResponseWrapper();
      DefaultLaputaSessionContext webSessionContext = new DefaultLaputaSessionContext(sessionContext);
      webSessionContext.setRequestWrapper(requestWrapper);
      webSessionContext.setResponseWrapper(responseWrapper);
      sessionContext = webSessionContext;
    }
    return sessionContext;
  }

  @Override
  protected SessionKey getSessionKey(SubjectContext subjectContext) {
    Serializable sessionId = subjectContext.getSessionId();
    RequestWrapper requestWrapper = getRequestWrapper(subjectContext);
    ResponseWrapper responseWrapper = getResponseWrapper(subjectContext);
    return new LaputaSessionKey(sessionId, requestWrapper, responseWrapper);
  }
}
