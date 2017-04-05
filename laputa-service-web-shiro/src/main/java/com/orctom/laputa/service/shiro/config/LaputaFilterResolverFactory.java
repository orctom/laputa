package com.orctom.laputa.service.shiro.config;

import com.orctom.laputa.service.shiro.mgt.LaputaFilterChainManager;
import com.orctom.laputa.service.shiro.mgt.LaputaFilterResolver;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniFactorySupport;

import java.util.Map;

public class LaputaFilterResolverFactory extends IniFactorySupport<LaputaFilterResolver> {

  private Map<String, ?> defaultBeans;

  public LaputaFilterResolverFactory(Ini ini) {
    super(ini);
  }

  public LaputaFilterResolverFactory(Ini ini, Map<String, ?> defaultBeans) {
    this(ini);
    this.defaultBeans = defaultBeans;
  }

  @Override
  protected LaputaFilterResolver createInstance(Ini ini) {
    LaputaFilterResolver resolver = createDefaultInstance();
    LaputaFilterChainManager manager = resolver.getFilterChainManager();
    buildChains(manager, ini);
    return resolver;
  }

  private void buildChains(LaputaFilterChainManager manager, Ini ini) {

  }

  @Override
  protected LaputaFilterResolver createDefaultInstance() {
    return new LaputaFilterResolver();
  }
}
