package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public class UserFilter extends AccessControlFilter {

  @Override
  public String getName() {
    return null;
  }

  @Override
  protected boolean isAccessAllowed(RequestWrapper requestWrapper, Context context, Object mappedValue) {
    return false;
  }

  @Override
  protected boolean onAccessDenied(RequestWrapper requestWrapper, Context context, Object mappedValue) {
    return false;
  }
}
