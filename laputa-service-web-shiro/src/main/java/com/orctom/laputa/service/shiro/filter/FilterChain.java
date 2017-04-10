package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

import java.util.List;

public class FilterChain {

  private List<Filter> filters;
  private int pos = 0;
  private int size = 0;

  public FilterChain(List<Filter> filters) {
    if (null == filters || filters.isEmpty()) {
      return;
    }
    this.filters = filters;
    size = filters.size();
  }

  public void doFilter(RequestWrapper requestWrapper, Context context) {
    if (pos == size) {
      return;
    }

    filters.get(pos++).filter(requestWrapper, context, this);
  }
}
