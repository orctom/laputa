package com.orctom.laputa.service.internal;

import com.orctom.laputa.service.filter.Filter;
import com.orctom.laputa.service.filter.FilterChain;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

import java.util.List;

public class LaputaFilterChain implements FilterChain {

  private DefaultRequestProcessor processor;
  private List<Filter> filters;
  private int pos = 0;
  private int size = 0;

  LaputaFilterChain(DefaultRequestProcessor processor) {
    this.processor = processor;
  }

  LaputaFilterChain(DefaultRequestProcessor processor, List<Filter> filters) {
    this.processor = processor;
    if (null == filters || filters.isEmpty()) {
      return;
    }
    this.filters = filters;
    size = filters.size();
  }

  @Override
  public void doFilter(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    if (pos == size) {
      processor.service(requestWrapper, responseWrapper);
    } else {
      filters.get(pos++).doFilter(requestWrapper, responseWrapper, this);
    }
  }
}
