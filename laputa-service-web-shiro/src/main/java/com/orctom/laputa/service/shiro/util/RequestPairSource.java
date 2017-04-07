package com.orctom.laputa.service.shiro.util;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public interface RequestPairSource {

  RequestWrapper getRequestWrapper();

  Context getContext();
}
