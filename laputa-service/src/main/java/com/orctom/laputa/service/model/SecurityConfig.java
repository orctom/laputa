package com.orctom.laputa.service.model;

import java.util.Collections;
import java.util.List;

public class SecurityConfig {

  private List<String> resources = Collections.emptyList();
  private List<String> nonResources = Collections.emptyList();

  public SecurityConfig(List<String> resources) {
    this.resources = resources;
  }

  public SecurityConfig(List<String> resources, List<String> nonResources) {
    if (null != resources && !resources.isEmpty()) {
      this.resources = resources;
    }
    if (null != nonResources && !nonResources.isEmpty()) {
      this.nonResources = nonResources;
    }
  }

  public List<String> getResources() {
    return resources;
  }

  public List<String> getNonResources() {
    return nonResources;
  }
}
