package com.orctom.laputa.service.shiro.session;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.subject.Subject;

public class LaputaSessionStorageEvaluator extends DefaultSessionStorageEvaluator {

  @Override
  public boolean isSessionStorageEnabled() {
    return true;
  }

  @Override
  public boolean isSessionStorageEnabled(Subject subject) {
    return true;
  }
}
