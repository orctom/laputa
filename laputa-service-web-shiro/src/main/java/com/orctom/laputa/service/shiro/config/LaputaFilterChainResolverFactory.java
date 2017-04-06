package com.orctom.laputa.service.shiro.config;

import com.orctom.laputa.service.shiro.filter.Filter;
import com.orctom.laputa.service.shiro.mgt.LaputaFilterChainManager;
import com.orctom.laputa.service.shiro.mgt.LaputaFilterChainResolver;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniFactorySupport;
import org.apache.shiro.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LaputaFilterChainResolverFactory extends IniFactorySupport<LaputaFilterChainResolver> {

  public static final String URLS = "urls";

  private Map<String, ?> defaultBeans;
  private Map<String, Filter> filters;

  private LaputaFilterChainResolverFactory(Ini ini) {
    super(ini);
  }

  public LaputaFilterChainResolverFactory(Ini ini, Map<String, ?> defaultBeans) {
    this(ini);
    this.defaultBeans = defaultBeans;
  }

  @Override
  protected LaputaFilterChainResolver createInstance(Ini ini) {
    LaputaFilterChainResolver resolver = createDefaultInstance();
    LaputaFilterChainManager manager = resolver.getFilterChainManager();
    buildChains(manager, ini);
    return resolver;
  }

  private void buildChains(LaputaFilterChainManager manager, Ini ini) {
    Map<String, Filter> filters = getFilters();

    registerFilters(filters, manager);

    Ini.Section section = ini.getSection(URLS);
    createChains(section, manager);
  }

  @Override
  protected LaputaFilterChainResolver createDefaultInstance() {
    return new LaputaFilterChainResolver();
  }

  private Map<String, Filter> getFilters() {
    Map<String, Filter> filterMap = new LinkedHashMap<>();
    for (Map.Entry<String, ?> entry : this.defaultBeans.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Filter) {
        filterMap.put(key, (Filter) value);
      }
    }
    return filterMap;
  }

  private void registerFilters(Map<String, Filter> filters, LaputaFilterChainManager manager) {
    if (CollectionUtils.isEmpty(filters)) {
      return;
    }

    for (Map.Entry<String, Filter> entry : filters.entrySet()) {
      String name = entry.getKey();
      Filter filter = entry.getValue();
      manager.addFilter(name, filter);
    }
  }

  private void createChains(Ini.Section urls, LaputaFilterChainManager manager) {
    if (CollectionUtils.isEmpty(urls)) {
      return;
    }
    for (Map.Entry<String, String> entry : urls.entrySet()) {
      String path = entry.getKey();
      String value = entry.getValue();
      manager.createChain(path, value);
    }
  }
}
