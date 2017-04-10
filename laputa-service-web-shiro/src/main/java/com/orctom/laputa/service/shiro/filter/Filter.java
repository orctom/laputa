package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public abstract class Filter {

  public abstract void filter(RequestWrapper requestWrapper, Context ctx, FilterChain filterChain);
}
