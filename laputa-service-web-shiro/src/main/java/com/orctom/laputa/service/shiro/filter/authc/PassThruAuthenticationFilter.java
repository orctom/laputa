package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.filter.FilterChain;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

public class PassThruAuthenticationFilter extends AuthenticationFilter {

  @Override
  protected void checkAccess(RequestWrapper requestWrapper,
                             ResponseWrapper responseWrapper,
                             Object mappedValue,
                             FilterChain filterChain) {
    if (!isLoginRequest(requestWrapper)) {
      saveRequestAndRedirectToLogin(requestWrapper, responseWrapper);
    }

    filterChain.doFilter(requestWrapper, responseWrapper);
  }
}
