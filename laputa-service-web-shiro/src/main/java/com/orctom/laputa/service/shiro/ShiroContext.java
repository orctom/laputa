package com.orctom.laputa.service.shiro;

import com.orctom.laputa.service.shiro.mgt.LaputaFilterChainResolver;
import org.apache.shiro.mgt.SecurityManager;

public abstract class ShiroContext {

  private static SecurityManager securityManager;

  private static LaputaFilterChainResolver filterChainResolver;

  public static SecurityManager getSecurityManager() {
    return securityManager;
  }

  public static void setSecurityManager(SecurityManager securityManager) {
    ShiroContext.securityManager = securityManager;
  }

  public static LaputaFilterChainResolver getFilterChainResolver() {
    return filterChainResolver;
  }

  public static void setFilterChainResolver(LaputaFilterChainResolver resolver) {
    ShiroContext.filterChainResolver = resolver;
  }
}
