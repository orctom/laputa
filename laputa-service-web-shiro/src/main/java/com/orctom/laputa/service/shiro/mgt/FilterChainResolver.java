package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.filter.FilterChain;

public interface FilterChainResolver {

  FilterChain getChain(RequestWrapper requestWrapper, Context ctx);
}
