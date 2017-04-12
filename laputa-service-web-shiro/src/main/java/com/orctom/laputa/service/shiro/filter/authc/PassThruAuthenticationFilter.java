package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

public class PassThruAuthenticationFilter extends AuthenticationFilter {

  @Override
  protected boolean onAccessDenied(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    if (isLoginRequest(requestWrapper)) {
      return true;
    }

    saveRequestAndRedirectToLogin(requestWrapper, responseWrapper);
    return false;
  }
}
