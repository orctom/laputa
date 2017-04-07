package com.orctom.laputa.service.shiro.subject;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.util.RequestPairSource;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;

public class LaputaSubjectContext extends DefaultSubjectContext implements RequestPairSource {

  private static final String KEY_REQUEST_WRAPPER = LaputaSubjectContext.class.getName() + ".REQUEST_WRAPPER";
  private static final String KEY_CONTEXT = LaputaSubjectContext.class.getName() + ".CONTEXT";

  public void setRequestWrapper(RequestWrapper requestWrapper) {
    super.put(KEY_REQUEST_WRAPPER, requestWrapper);
  }

  public RequestWrapper getRequestWrapper() {
    RequestWrapper requestWrapper = super.getTypedValue(KEY_REQUEST_WRAPPER, RequestWrapper.class);
    if (null != requestWrapper) {
      return requestWrapper;
    }

    Subject existing = getSubject();
    if (existing instanceof LaputaSubject) {
      return ((LaputaSubject) existing).getRequestWrapper();
    }

    return null;
  }

  public void setContext(Context context) {
    super.put(KEY_CONTEXT, context);
  }

  public Context getContext() {
    Context context = super.getTypedValue(KEY_CONTEXT, Context.class);
    if (null != context) {
      return context;
    }

    Subject existing = getSubject();
    if (existing instanceof LaputaSubject) {
      return ((LaputaSubject) existing).getContext();
    }

    return null;
  }
}
