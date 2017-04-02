package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public abstract class Filter {

  protected FilterConfig filterConfig;

  public abstract String getName();

  public FilterConfig getFilterConfig() {
    return filterConfig;
  }

  public void setFilterConfig(FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
  }

  protected void setRedirect(Context context, String redirectTo) {
    context.setRedirectTo(redirectTo);
  }

  public abstract void filter(RequestWrapper requestWrapper, Context ctx);
}
