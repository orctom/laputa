package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.filter.FilterChain;
import com.orctom.laputa.service.shiro.filter.PathMatchingFilter;

public class AnonymousFilter extends PathMatchingFilter {

  @Override
  protected void doFilter(RequestWrapper requestWrapper, Context ctx, FilterChain filterChain) {
    // do nothing
  }
}
