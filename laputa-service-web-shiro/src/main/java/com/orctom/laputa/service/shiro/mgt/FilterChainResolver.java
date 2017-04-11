package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.filter.FilterChain;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

public interface FilterChainResolver {

  FilterChain getChain(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, FilterChain filterChain);
}
