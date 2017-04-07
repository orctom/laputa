package com.orctom.laputa.service.shiro.subject;


import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.util.RequestPairSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;

public interface LaputaSubject extends Subject, RequestPairSource {

  RequestWrapper getRequestWrapper();
  Context getContext();

  public static class Builder extends Subject.Builder {

    private RequestWrapper requestWrapper;
    private Context context;

    public Builder(RequestWrapper requestWrapper, Context context) {
      this(SecurityUtils.getSecurityManager(), requestWrapper, context);
    }

    public Builder(SecurityManager securityManager, RequestWrapper requestWrapper, Context context) {
      super(securityManager);
      this.requestWrapper = requestWrapper;
      this.context = context;
      setRequestWrapper(requestWrapper);
      setContext(context);
    }

    private void setRequestWrapper(RequestWrapper requestWrapper) {
      ((LaputaSubjectContext) getSubjectContext()).setRequestWrapper(requestWrapper);
    }

    private void setContext(Context context) {
      ((LaputaSubjectContext) getSubjectContext()).setContext(context);
    }

    @Override
    protected SubjectContext newSubjectContextInstance() {
      return new LaputaSubjectContext();
    }

    @Override
    public LaputaSubject buildSubject() {
      Subject subject = super.buildSubject();
      if (!(subject instanceof LaputaSubject)) {
        String msg = "Subject implementation returned from the SecurityManager was not a " +
            LaputaSubject.class.getName() + " implementation.  Please ensure a Web-enabled SecurityManager " +
            "has been configured and made available to this builder.";
        throw new IllegalStateException(msg);
      }
      return (LaputaSubject) subject;
    }
  }
}
