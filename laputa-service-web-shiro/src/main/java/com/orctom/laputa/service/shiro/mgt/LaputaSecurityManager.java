package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.shiro.subject.LaputaSubjectContext;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.SubjectContext;

import java.util.Collection;

public class LaputaSecurityManager extends DefaultSecurityManager {

  public LaputaSecurityManager() {
    super();
    // todo

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

  /**
   * @param sessionManager
   * @since 1.2
   */
  private void setInternalSessionManager(SessionManager sessionManager) {
    super.setSessionManager(sessionManager);
  }

  protected SessionManager createSessionManager(String sessionMode) {
    return new LaputaSessionManager();
  }
}
