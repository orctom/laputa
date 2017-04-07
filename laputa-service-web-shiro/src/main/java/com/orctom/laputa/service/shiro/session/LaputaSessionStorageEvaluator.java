package com.orctom.laputa.service.shiro.session;

import com.orctom.laputa.service.shiro.subject.LaputaSubject;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.session.mgt.NativeSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;

public class LaputaSessionStorageEvaluator extends DefaultSessionStorageEvaluator {

  private SessionManager sessionManager;

  public SessionManager getSessionManager() {
    return sessionManager;
  }

  public void setSessionManager(SessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  @Override
  public boolean isSessionStorageEnabled(Subject subject) {
    if (subject.getSession(false) != null) {
      //use what already exists
      return true;
    }

    if (!isSessionStorageEnabled()) {
      //honor global setting:
      return false;
    }

    if (!(subject instanceof LaputaSubject) && (this.sessionManager != null && !(this.sessionManager instanceof NativeSessionManager))) {
      return false;
    }

    return true;
  }
}
