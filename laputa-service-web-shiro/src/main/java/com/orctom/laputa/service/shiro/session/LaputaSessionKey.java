package com.orctom.laputa.service.shiro.session;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.shiro.util.RequestPairSource;
import org.apache.shiro.session.mgt.DefaultSessionKey;

import java.io.Serializable;

public class LaputaSessionKey extends DefaultSessionKey implements RequestPairSource {

  private RequestWrapper requestWrapper;
  private ResponseWrapper responseWrapper;

  public LaputaSessionKey(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    if (requestWrapper == null) {
      throw new NullPointerException("request wrapper argument cannot be null.");
    }
    if (responseWrapper == null) {
      throw new NullPointerException("response wrapper argument cannot be null.");
    }
    this.requestWrapper = requestWrapper;
    this.responseWrapper = responseWrapper;
  }

  public LaputaSessionKey(Serializable sessionId, RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    this(requestWrapper, responseWrapper);
    setSessionId(sessionId);
  }

  @Override
  public RequestWrapper getRequestWrapper() {
    return requestWrapper;
  }

  @Override
  public ResponseWrapper getResponseWrapper() {
    return responseWrapper;
  }
}
