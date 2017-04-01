package com.orctom.laputa.service.shiro.filter;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class PathMatchingFilter extends Filter {

  protected PatternMatcher pathMatcher = new AntPathMatcher();

  protected Map<String, String[]> resources = new LinkedHashMap<>();

  protected boolean pathsMatch(String pattern, String path) {
    return pathMatcher.matches(pattern, path);
  }

  protected abstract boolean isAccessAllowed();

  @Override
  public void filter(RequestWrapper requestWrapper, Context ctx) {

  }
}
