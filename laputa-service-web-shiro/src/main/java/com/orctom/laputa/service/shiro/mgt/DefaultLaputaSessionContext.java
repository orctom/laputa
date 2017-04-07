package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.session.LaputaSessionContext;
import com.orctom.laputa.service.shiro.util.RequestPairSource;
import org.apache.shiro.session.mgt.DefaultSessionContext;

import java.util.Map;

public class DefaultLaputaSessionContext extends DefaultSessionContext
    implements LaputaSessionContext, RequestPairSource {

  private static final String KEY_REQUEST_WRAPPER = DefaultLaputaSessionContext.class.getName() + ".REQUEST_WRAPPER";
  private static final String KEY_CONTEXT = DefaultLaputaSessionContext.class.getName() + ".CONTEXT";

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
  public Context getContext() {
    return super.getTypedValue(KEY_CONTEXT, Context.class);
  }

  @Override
  public void setContext(Context context) {
    super.put(KEY_CONTEXT, context);
  }
}
