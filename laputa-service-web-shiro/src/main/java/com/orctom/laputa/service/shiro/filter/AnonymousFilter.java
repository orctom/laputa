package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public class AnonymousFilter extends PathMatchingFilter {

  @Override
  public String getName() {
    return "anon";
  }

  @Override
  protected boolean preHandle(RequestWrapper requestWrapper, Context ctx) {
    return true;
  }
}
