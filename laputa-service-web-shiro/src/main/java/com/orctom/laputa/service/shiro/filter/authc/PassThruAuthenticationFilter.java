package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.filter.FilterChain;

public class PassThruAuthenticationFilter extends AuthenticationFilter {

  @Override
  protected void checkAccess(RequestWrapper requestWrapper,
                             Context context,
                             Object mappedValue,
                             FilterChain filterChain) {
    if (!isLoginRequest(requestWrapper)) {
      saveRequestAndRedirectToLogin(requestWrapper, context);
    }

    filterChain.doFilter(requestWrapper, context);
  }
}
