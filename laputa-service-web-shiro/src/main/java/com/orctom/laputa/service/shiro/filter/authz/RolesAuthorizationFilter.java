package com.orctom.laputa.service.shiro.filter.authz;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;

import java.util.Set;

public class RolesAuthorizationFilter extends AuthorizationFilter {

  @Override
  public String getName() {
    return "role";
  }

  @Override
  protected boolean isAccessAllowed(RequestWrapper requestWrapper, Context context, Object mappedValue) {
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
