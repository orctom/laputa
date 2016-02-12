package com.orctom.laputa.server.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Trie tree node
 * Created by hao on 9/23/15.
 */
public class PathTrie {

  private RequestMapping handler;

  private Map<String, PathTrie> children = new HashMap<>();

  public PathTrie() {
  }

  public PathTrie(RequestMapping handler) {
    this.handler = handler;
  }

  public PathTrie(String uri, Class<?> handlerClass, Method handlerMethod) {
    this.handler = new RequestMapping(uri, handlerClass, handlerMethod);
  }

  public RequestMapping getHandler() {
    return handler;
  }

  public Map<String, PathTrie> getChildren() {
    return children;
  }

  @Override
  public String toString() {
    return getChildMappings(this);
  }

  public String getChildMappings(PathTrie parent) {
    StringBuilder str = new StringBuilder();
    if (null != parent.getHandler()) {
      str.append("\n").append(parent.getHandler().toString());
    }
    for (Map.Entry<String, PathTrie> entry : parent.getChildren().entrySet()) {
      str.append(getChildMappings(entry.getValue()));
    }
    return str.toString();
  }
}
