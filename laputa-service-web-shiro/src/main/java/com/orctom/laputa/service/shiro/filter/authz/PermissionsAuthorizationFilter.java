package com.orctom.laputa.service.shiro.filter.authz;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import org.apache.shiro.subject.Subject;

public class PermissionsAuthorizationFilter extends AuthorizationFilter {

  @Override
  protected boolean isAccessAllowed(RequestWrapper requestWrapper,
                                    ResponseWrapper responseWrapper,
                                    Object mappedValue) {
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
