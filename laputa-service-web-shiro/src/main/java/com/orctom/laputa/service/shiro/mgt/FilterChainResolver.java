package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public interface FilterChainResolver {

  NamedFilterList getChain(RequestWrapper requestWrapper, Context ctx);
}
