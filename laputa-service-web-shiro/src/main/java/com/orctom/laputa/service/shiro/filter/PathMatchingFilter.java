package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.filter.Filter;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.shiro.util.StringUtils.split;

public abstract class PathMatchingFilter extends AbstractFilter {

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
  protected boolean preHandle(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    if (this.appliedPaths == null || this.appliedPaths.isEmpty()) {
      return true;
    }

    for (String path : this.appliedPaths.keySet()) {
      if (pathsMatch(path, requestWrapper.getPath())) {
        Object config = this.appliedPaths.get(path);
        return onPreHandle(requestWrapper, responseWrapper, config);
      }
    }

    return true;
  }

  protected boolean onPreHandle(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, Object mappedValue) {
    return true;
  }
}
