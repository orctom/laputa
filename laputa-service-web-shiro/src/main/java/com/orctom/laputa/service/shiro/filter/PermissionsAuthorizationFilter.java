package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public class PermissionsAuthorizationFilter extends AuthenticationFilter {

  @Override
  public String getName() {
    return null;
  }

  @Override
  protected boolean onAccessDenied(RequestWrapper requestWrapper, Context context, Object mappedValue) {
    return false;
  }
}
