package com.orctom.laputa.service.filter;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

public interface FilterChain {

  void doFilter(RequestWrapper requestWrapper, ResponseWrapper responseWrapper);
}
