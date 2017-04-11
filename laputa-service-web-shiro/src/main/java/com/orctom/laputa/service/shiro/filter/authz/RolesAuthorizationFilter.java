package com.orctom.laputa.service.shiro.filter.authz;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;

import java.util.Set;

public class RolesAuthorizationFilter extends AuthorizationFilter {

  @Override
  protected boolean isAccessAllowed(RequestWrapper requestWrapper,
                                    ResponseWrapper responseWrapper,
                                    Object mappedValue) {
    Subject subject = getSubject(requestWrapper);
    String[] rolesArray = (String[]) mappedValue;

    if (rolesArray == null || rolesArray.length == 0) {
      //no roles specified, so nothing to check - allow access.
      return true;
    }

    Set<String> roles = CollectionUtils.asSet(rolesArray);
    return subject.hasAllRoles(roles);
  }

}
