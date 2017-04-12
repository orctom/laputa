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
  public final void doFilter(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, FilterChain filterChain) {
    try {
      boolean continueChain = preHandle(requestWrapper, responseWrapper);
      if (continueChain) {
        executeChain(requestWrapper, responseWrapper, filterChain);
      }
      postHandle(requestWrapper, responseWrapper);
    } catch (Exception e) {
      onException(requestWrapper, responseWrapper, e);
    }
  }

  protected boolean preHandle(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    return true;
  }

  protected void executeChain(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, FilterChain filterChain) {
    filterChain.doFilter(requestWrapper, responseWrapper);
  }

  protected void postHandle(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
  }

  protected void onException(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, Exception e) {
    LOGGER.error(e.getMessage(), e);
    responseWrapper.setRedirectTo(PATH_500);
  }
}
