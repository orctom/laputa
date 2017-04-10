package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.filter.FilterChain;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;

import java.util.Map;

public class LaputaFilterChainResolver implements FilterChainResolver {

  private LaputaFilterChainManager filterChainManager;

  private PatternMatcher pathMatcher;

  public LaputaFilterChainResolver() {
    this.filterChainManager = new LaputaFilterChainManager();
    this.pathMatcher = new AntPathMatcher();
  }

  public LaputaFilterChainManager getFilterChainManager() {
    return filterChainManager;
  }

  public PatternMatcher getPathMatcher() {
    return pathMatcher;
  }

  @Override
  public FilterChain getChain(RequestWrapper requestWrapper, Context ctx) {
    if (!filterChainManager.hasChains()) {
      return null;
    }

    String path = requestWrapper.getPath();

    Map<String, NamedFilterList> filterChains = filterChainManager.getFilterChains();
    for (Map.Entry<String, NamedFilterList> entry : filterChains.entrySet()) {
      String pathPattern = entry.getKey();
      NamedFilterList filterList = entry.getValue();
      if (pathMatches(pathPattern, path)) {
        return new FilterChain(filterList.getFilters());
      }
    }

    return null;
  }

  protected boolean pathMatches(String pattern, String path) {
    PatternMatcher pathMatcher = getPathMatcher();
    return pathMatcher.matches(pattern, path);
  }
}
