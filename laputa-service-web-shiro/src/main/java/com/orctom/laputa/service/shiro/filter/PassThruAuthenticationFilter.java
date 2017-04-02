package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public class PassThruAuthenticationFilter extends AuthenticationFilter {

  @Override
  public String getName() {
    return "passThru";
  }

  @Override
  protected boolean onAccessDenied(RequestWrapper requestWrapper, Context context, Object mappedValue) {
    if (isLoginRequest(requestWrapper)) {
      return true;
    } else {
      saveRequestAndRedirectToLogin(requestWrapper, context);
      return false;
    }
  }
}
