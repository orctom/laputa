package com.orctom.laputa.service.shiro.session;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.shiro.util.RequestPairSource;
import org.apache.shiro.session.mgt.DefaultSessionContext;

import java.util.Map;

public class DefaultLaputaSessionContext extends DefaultSessionContext
    implements LaputaSessionContext, RequestPairSource {

  private static final String KEY_REQUEST_WRAPPER = DefaultLaputaSessionContext.class.getName() + ".REQUEST_WRAPPER";
  private static final String KEY_RESPONSE_WRAPPER = DefaultLaputaSessionContext.class.getName() + ".RESPONSE_WRAPPER";

  public DefaultLaputaSessionContext() {
    super();
  }

  public DefaultLaputaSessionContext(Map<String, Object> map) {
    super(map);
  }

  @Override
  public RequestWrapper getRequestWrapper() {
    return super.getTypedValue(KEY_REQUEST_WRAPPER, RequestWrapper.class);
  }

  @Override
  public void setRequestWrapper(RequestWrapper requestWrapper) {
    super.put(KEY_REQUEST_WRAPPER, requestWrapper);
  }

  @Override
  public ResponseWrapper getResponseWrapper() {
    return super.getTypedValue(KEY_RESPONSE_WRAPPER, ResponseWrapper.class);
  }

  @Override
  public void setResponseWrapper(ResponseWrapper responseWrapper) {
    super.put(KEY_RESPONSE_WRAPPER, responseWrapper);
  }
}
