package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.filter.FilterChain;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.shiro.filter.PathMatchingFilter;

public class AnonymousFilter extends PathMatchingFilter {

  @Override
  protected void doFilter(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, FilterChain filterChain) {
    // do nothing
  }
}
