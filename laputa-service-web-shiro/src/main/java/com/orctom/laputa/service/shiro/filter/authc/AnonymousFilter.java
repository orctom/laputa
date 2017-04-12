package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.shiro.filter.PathMatchingFilter;

public class AnonymousFilter extends PathMatchingFilter {

  @Override
  protected boolean onPreHandle(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, Object mappedValue) {
    return true;
  }
}
