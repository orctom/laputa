package com.orctom.laputa.service.shiro.util;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

public abstract class RequestPairSourceUtils {

  public static RequestWrapper getRequestWrapper(Object source) {
    if (source instanceof RequestPairSource) {
      return ((RequestPairSource) source).getRequestWrapper();
    }
    return null;
  }

  public static ResponseWrapper getResponseWrapper(Object source) {
    if (source instanceof RequestPairSource) {
      return ((RequestPairSource) source).getResponseWrapper();
    }
    return null;
  }

}
