package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public abstract class AdviceFilter extends Filter {

  protected boolean preHandle(RequestWrapper requestWrapper, Context ctx) {
    return true;
  }

  protected void postHandle(RequestWrapper requestWrapper, Context ctx) {
  }

  public void afterCompletion(RequestWrapper requestWrapper, Context ctx, Exception exception) {
  }

  protected void continueChain(RequestWrapper requestWrapper, Context ctx) {
    // todo
  }

  @Override
  public void filter(RequestWrapper requestWrapper, Context ctx) {
    Exception exception = null;
    try {
      if (preHandle(requestWrapper, ctx)) {
        continueChain(requestWrapper, ctx);
      }
      postHandle(requestWrapper, ctx);
    } catch (Exception e) {
      exception = e;
    } finally {
      afterCompletion(requestWrapper, ctx, exception);
    }
  }
}
