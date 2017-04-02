package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.shiro.util.StringUtils.split;

public abstract class PathMatchingFilter extends AdviceFilter {

  protected PatternMatcher pathMatcher = new AntPathMatcher();

  protected Map<String, Object> appliedPaths = new LinkedHashMap<String, Object>();

  protected Map<String, String[]> resources = new LinkedHashMap<>();

  public Filter processPathConfig(String path, String config) {
    String[] values = null;
    if (config != null) {
      values = split(config);
    }

    this.appliedPaths.put(path, values);
    return this;
  }

  protected boolean pathsMatch(String pattern, String path) {
    return pathMatcher.matches(pattern, path);
  }

  @Override
  protected boolean preHandle(RequestWrapper requestWrapper, Context ctx) {
    if (this.appliedPaths == null || this.appliedPaths.isEmpty()) {
      return true;
    }

    for (String path : this.appliedPaths.keySet()) {
      if (pathsMatch(path, requestWrapper.getPath())) {
        Object config = this.appliedPaths.get(path);
        return isFilterChainContinued(requestWrapper, ctx, config);
      }
    }

    //no path matched, allow the request to go through:
    return true;
  }

  private boolean isFilterChainContinued(RequestWrapper requestWrapper, Context ctx, Object pathConfig) {
    return onPreHandle(requestWrapper, ctx, pathConfig);
  }

  protected boolean onPreHandle(RequestWrapper requestWrapper, Context ctx, Object mappedValue) {
    return true;
  }
}
