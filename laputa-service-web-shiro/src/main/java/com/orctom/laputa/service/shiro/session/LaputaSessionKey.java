package com.orctom.laputa.service.shiro.session;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.util.RequestPairSource;
import org.apache.shiro.session.mgt.DefaultSessionKey;

import java.io.Serializable;

public class LaputaSessionKey extends DefaultSessionKey implements RequestPairSource {

  private RequestWrapper requestWrapper;
  private Context context;

  public LaputaSessionKey(RequestWrapper requestWrapper, Context context) {
    if (requestWrapper == null) {
      throw new NullPointerException("request wrapper argument cannot be null.");
    }
    if (context == null) {
      throw new NullPointerException("context argument cannot be null.");
    }
    this.requestWrapper = requestWrapper;
    this.context = context;
  }

  public LaputaSessionKey(Serializable sessionId, RequestWrapper requestWrapper, Context context) {
    this(requestWrapper, context);
    setSessionId(sessionId);
  }

  @Override
  public RequestWrapper getRequestWrapper() {
    return requestWrapper;
  }

  @Override
  public Context getContext() {
    return context;
  }
}
