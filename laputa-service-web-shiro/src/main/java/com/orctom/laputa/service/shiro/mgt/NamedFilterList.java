package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.filter.Filter;
import org.apache.shiro.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NamedFilterList {

  private String name;
  private List<Filter> filters;

  public NamedFilterList(String name) {
    this(name, new ArrayList<>());
  }

  public NamedFilterList(String name, List<Filter> filters) {
    this.name = name;
    this.filters = filters;
  }

  protected void setName(String name) {
    if (!StringUtils.hasText(name)) {
      throw new IllegalArgumentException("Cannot specify a null or empty name.");
    }
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public boolean add(Filter filter) {
    return this.filters.add(filter);
  }

  public List<Filter> getFilters() {
    return filters;
  }

  public int size() {
    return this.filters.size();
  }
}
