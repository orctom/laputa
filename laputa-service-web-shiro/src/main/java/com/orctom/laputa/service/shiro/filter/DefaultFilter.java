package com.orctom.laputa.service.shiro.filter;

import org.apache.shiro.util.ClassUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public enum DefaultFilter {
  anon(AnonymousFilter.class),
  authc(FormAuthenticationFilter.class),
  logout(LogoutFilter.class),
  perms(PermissionsAuthorizationFilter.class),
  roles(RolesAuthorizationFilter.class),
  user(UserFilter.class);

  private final Class<? extends Filter> filterClass;

  DefaultFilter(Class<? extends Filter> filterClass) {
    this.filterClass = filterClass;
  }

  public Filter newInstance() {
    return (Filter) ClassUtils.newInstance(this.filterClass);
  }

  public Class<? extends Filter> getFilterClass() {
    return this.filterClass;
  }

  public static Map<String, Filter> createInstanceMap() {
    Map<String, Filter> filters = new LinkedHashMap<>(values().length);
    for (DefaultFilter defaultFilter : values()) {
      Filter filter = defaultFilter.newInstance();
      filters.put(defaultFilter.name(), filter);
    }
    return filters;
  }
}
