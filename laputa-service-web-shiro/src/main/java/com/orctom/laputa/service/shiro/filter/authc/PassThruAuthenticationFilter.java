package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public class PassThruAuthenticationFilter extends AuthenticationFilter {

  @Override
  protected void checkAccess(RequestWrapper requestWrapper, Context context, Object mappedValue) {
    if (!isLoginRequest(requestWrapper)) {
      saveRequestAndRedirectToLogin(requestWrapper, context);
    }
  }
}
