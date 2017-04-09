package com.orctom.laputa.service.shiro.session;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.util.RequestPairSource;
import org.apache.shiro.session.mgt.SessionContext;

public interface LaputaSessionContext extends SessionContext, RequestPairSource {

  RequestWrapper getRequestWrapper();

  void setRequestWrapper(RequestWrapper requestWrapper);

  Context getContext();

  void setContext(Context context);
}
