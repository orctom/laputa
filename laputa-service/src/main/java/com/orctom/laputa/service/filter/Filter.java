package com.orctom.laputa.service.filter;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

@FunctionalInterface
public interface Filter {

  default int getOrder() {
    return 0;
  }

  void filter(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, FilterChain filterChain);
}
