package com.orctom.laputa.service.shiro.util;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

public interface RequestPairSource {

  RequestWrapper getRequestWrapper();

  ResponseWrapper getResponseWrapper();
}
