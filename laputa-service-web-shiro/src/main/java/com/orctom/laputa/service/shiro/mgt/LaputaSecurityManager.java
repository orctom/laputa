package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.session.LaputaSessionKey;
import com.orctom.laputa.service.shiro.session.LaputaSessionManager;
import com.orctom.laputa.service.shiro.session.LaputaSessionStorageEvaluator;
import com.orctom.laputa.service.shiro.subject.LaputaSubjectContext;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.SubjectContext;

import java.io.Serializable;
import java.util.Collection;

import static com.orctom.laputa.service.shiro.util.RequestPairSourceUtils.getContext;
import static com.orctom.laputa.service.shiro.util.RequestPairSourceUtils.getRequestWrapper;

public class LaputaSecurityManager extends DefaultSecurityManager {

  public LaputaSecurityManager() {
    super();
    ((DefaultSubjectDAO) this.subjectDAO).setSessionStorageEvaluator(new LaputaSessionStorageEvaluator());
    setSubjectFactory(new laputaSubjectFactory());
    setRememberMeManager(new CookieRememberMeManager());
    setSessionManager(new LaputaSessionManager());
  }

  public LaputaSecurityManager(Realm singleRealm) {
    this();
    setRealm(singleRealm);
  }

  public LaputaSecurityManager(Collection<Realm> realms) {
    this();
    setRealms(realms);
  }

  @Override
  protected SubjectContext createSubjectContext() {
    return new LaputaSubjectContext();
  }

  @Override
  public void setSubjectDAO(SubjectDAO subjectDAO) {
    super.setSubjectDAO(subjectDAO);
    applySessionManagerToSessionStorageEvaluatorIfPossible();
  }

  @Override
  protected void afterSessionManagerSet() {
    super.afterSessionManagerSet();
    applySessionManagerToSessionStorageEvaluatorIfPossible();
  }

  private void applySessionManagerToSessionStorageEvaluatorIfPossible() {
    SubjectDAO subjectDAO = getSubjectDAO();
    if (subjectDAO instanceof DefaultSubjectDAO) {
      SessionStorageEvaluator evaluator = ((DefaultSubjectDAO) subjectDAO).getSessionStorageEvaluator();
      if (evaluator instanceof LaputaSessionStorageEvaluator) {
        ((LaputaSessionStorageEvaluator) evaluator).setSessionManager(getSessionManager());
      }
    }
  }

  @Override
  protected SubjectContext copy(SubjectContext subjectContext) {
    if (subjectContext instanceof LaputaSubjectContext) {
      return subjectContext;
    }
    return super.copy(subjectContext);
  }

  @Override
  public void setSessionManager(SessionManager sessionManager) {
    setInternalSessionManager(sessionManager);
  }

  private void setInternalSessionManager(SessionManager sessionManager) {
    super.setSessionManager(sessionManager);
  }

  protected SessionManager createSessionManager(String sessionMode) {
    return new LaputaSessionManager();
  }


  @Override
  protected SessionContext createSessionContext(SubjectContext subjectContext) {
    SessionContext sessionContext = super.createSessionContext(subjectContext);
    if (subjectContext instanceof LaputaSubjectContext) {
      LaputaSubjectContext lsc = (LaputaSubjectContext) subjectContext;
      RequestWrapper requestWrapper = lsc.getRequestWrapper();
      Context context = lsc.getContext();
      DefaultLaputaSessionContext webSessionContext = new DefaultLaputaSessionContext(sessionContext);
      webSessionContext.setRequestWrapper(requestWrapper);
      webSessionContext.setContext(context);
      sessionContext = webSessionContext;
    }
    return sessionContext;
  }

  @Override
  protected SessionKey getSessionKey(SubjectContext subjectContext) {
    Serializable sessionId = subjectContext.getSessionId();
    RequestWrapper requestWrapper = getRequestWrapper(subjectContext);
    Context context = getContext(subjectContext);
    return new LaputaSessionKey(sessionId, requestWrapper, context);
  }
}
