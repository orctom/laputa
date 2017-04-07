package com.orctom.laputa.service.shiro.util;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

public abstract class RequestPairSourceUtils {

  public static RequestWrapper getRequestWrapper(Object source) {
    if (source instanceof RequestPairSource) {
      return ((RequestPairSource) source).getRequestWrapper();
    }
    return null;
  }

  public static Context getContext(Object source) {
    if (source instanceof RequestPairSource) {
      return ((RequestPairSource) source).getContext();
    }
    return null;
  }

}
