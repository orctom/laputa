package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.filter.Filter;
import com.orctom.laputa.service.filter.FilterChain;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProxiedFilterChain implements FilterChain {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProxiedFilterChain.class);

  private FilterChain orig;
  private List<Filter> filters;
  private int index = 0;

  public ProxiedFilterChain(FilterChain orig, List<Filter> filters) {
    if (orig == null) {
      throw new NullPointerException("original FilterChain cannot be null.");
    }
    this.orig = orig;
    this.filters = filters;
    this.index = 0;
  }

  @Override
  public void doFilter(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    if (this.filters == null || this.filters.size() == this.index) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Invoking original filter chain.");
      }
      this.orig.doFilter(requestWrapper, responseWrapper);
    } else {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Invoking wrapped filter at index [" + this.index + "]");
      }
      this.filters.get(this.index++).filter(requestWrapper, responseWrapper, this);
    }
  }
}
