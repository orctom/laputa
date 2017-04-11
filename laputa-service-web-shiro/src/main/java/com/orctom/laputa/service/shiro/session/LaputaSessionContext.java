package com.orctom.laputa.service.shiro.session;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.shiro.util.RequestPairSource;
import org.apache.shiro.session.mgt.SessionContext;

public interface LaputaSessionContext extends SessionContext, RequestPairSource {

  void setRequestWrapper(RequestWrapper requestWrapper);

  void setResponseWrapper(ResponseWrapper responseWrapper);
}
