package com.orctom.laputa.service.shiro.subject;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.shiro.util.RequestPairSource;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;

public class LaputaSubjectContext extends DefaultSubjectContext implements RequestPairSource {

  private static final String KEY_REQUEST_WRAPPER = LaputaSubjectContext.class.getName() + ".REQUEST_WRAPPER";
  private static final String KEY_RESPONSE_WRAPPER = LaputaSubjectContext.class.getName() + ".RESPONSE_WRAPPER";

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

  public void setResponseWrapper(ResponseWrapper responseWrapper) {
    super.put(KEY_RESPONSE_WRAPPER, responseWrapper);
  }

  public ResponseWrapper getResponseWrapper() {
    ResponseWrapper responseWrapper = super.getTypedValue(KEY_RESPONSE_WRAPPER, ResponseWrapper.class);
    if (null != responseWrapper) {
      return responseWrapper;
    }

    Subject existing = getSubject();
    if (existing instanceof LaputaSubject) {
      return ((LaputaSubject) existing).getResponseWrapper();
    }

    return null;
  }
}
