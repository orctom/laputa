package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.orctom.laputa.service.Constants.PATH_500;

public abstract class AbstractFilter extends Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFilter.class);

  @Override
  public final void filter(RequestWrapper requestWrapper, Context ctx) {
    try {
      doFilter(requestWrapper, ctx);
    } catch (Exception e) {
      onException(requestWrapper, ctx, e);
    }
  }

  protected void doFilter(RequestWrapper requestWrapper, Context ctx) {
  }

  protected void onException(RequestWrapper requestWrapper, Context ctx, Exception e) {
    LOGGER.error(e.getMessage(), e);
    ctx.setRedirectTo(PATH_500);
  }
}
