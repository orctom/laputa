package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.filter.Filter;
import com.orctom.laputa.service.filter.FilterChain;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.orctom.laputa.service.Constants.PATH_500;

public abstract class AbstractFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFilter.class);

  @Override
  public final void filter(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, FilterChain filterChain) {
    try {
      doFilter(requestWrapper, responseWrapper, filterChain);
    } catch (Exception e) {
      onException(requestWrapper, responseWrapper, e);
    }
  }

  protected void doFilter(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, FilterChain filterChain) {
  }

  protected void onException(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, Exception e) {
    LOGGER.error(e.getMessage(), e);
    responseWrapper.setRedirectTo(PATH_500);
  }
}
