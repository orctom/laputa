package com.orctom.laputa.service.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

  public PathTrie(String uri,
                  Object instance,
                  Class<?> handlerClass,
                  Method handlerMethod,
                  String httpMethod,
                  String redirectTo,
                  boolean honorException) {
    this.handler = RequestMapping.builder()
        .uriPattern(uri)
        .target(instance)
        .handlerClass(handlerClass)
        .handlerMethod(handlerMethod)
        .httpMethod(httpMethod)
        .redirectTo(redirectTo)
        .honorExtension(honorException)
        .build();
  }

  public RequestMapping getHandler() {
    return handler;
  }

  public Map<String, PathTrie> getChildren() {
    return children;
  }

  @Override
  public String toString() {
    return getChildren().keySet().toString();
  }

  public List<RequestMapping> getChildrenMappings() {
    return getChildrenMappings(this);
  }

  private List<RequestMapping> getChildrenMappings(PathTrie parent) {
    List<RequestMapping> mappings = new ArrayList<>();
    if (null != parent.getHandler()) {
      mappings.add(parent.getHandler());
    }
    for (Map.Entry<String, PathTrie> entry : parent.getChildren().entrySet()) {
      mappings.addAll(getChildrenMappings(entry.getValue()));
    }
    return mappings;
  }
}
