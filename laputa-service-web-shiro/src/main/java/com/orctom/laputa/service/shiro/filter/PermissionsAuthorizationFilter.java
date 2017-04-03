package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import org.apache.shiro.subject.Subject;

public class PermissionsAuthorizationFilter extends AuthorizationFilter {

  @Override
  public String getName() {
    return "perms";
  }

  @Override
  protected boolean isAccessAllowed(RequestWrapper requestWrapper, Context context, Object mappedValue) {
    Subject subject = getSubject(requestWrapper);
    String[] perms = (String[]) mappedValue;

    boolean isPermitted = true;
    if (perms != null && perms.length > 0) {
      if (perms.length == 1) {
        if (!subject.isPermitted(perms[0])) {
          isPermitted = false;
        }
      } else {
        if (!subject.isPermittedAll(perms)) {
          isPermitted = false;
        }
      }
    }

    return isPermitted;
  }
}
