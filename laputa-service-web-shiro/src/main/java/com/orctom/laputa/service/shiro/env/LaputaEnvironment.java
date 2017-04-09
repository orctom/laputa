package com.orctom.laputa.service.shiro.env;

import com.orctom.laputa.service.shiro.ShiroContext;
import com.orctom.laputa.service.shiro.mgt.LaputaFilterChainResolver;
import com.orctom.laputa.service.shiro.mgt.LaputaFilterChainResolverFactory;
import com.orctom.laputa.service.shiro.mgt.LaputaIniSecurityManagerFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniFactorySupport;
import org.apache.shiro.env.DefaultEnvironment;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Initializable;

import java.util.Map;

import static com.orctom.laputa.service.shiro.mgt.LaputaFilterChainResolverFactory.URLS;

public class LaputaEnvironment extends DefaultEnvironment implements Initializable {

  private Ini ini = Ini.fromResourcePath(IniFactorySupport.DEFAULT_INI_RESOURCE_PATH);

  @Override
  public void init() throws ShiroException {
    LaputaIniSecurityManagerFactory factory = new LaputaIniSecurityManagerFactory(ini);
    SecurityManager securityManager = factory.getInstance();

    setSecurityManager(securityManager);
    setBeans(factory);
    setFilterChainResolver();
  }

  @Override
  public void setSecurityManager(SecurityManager securityManager) {
    super.setSecurityManager(securityManager);
    SecurityUtils.setSecurityManager(securityManager);
    ShiroContext.setSecurityManager(securityManager);
  }

  private void setBeans(LaputaIniSecurityManagerFactory factory) {
    this.objects.clear();
    Map<String, ?> beans = factory.getBeans();
    if (!CollectionUtils.isEmpty(beans)) {
      this.objects.putAll(beans);
    }
  }

  private void setFilterChainResolver() {
    LaputaFilterChainResolver resolver = createFilterChainResolver();
    ShiroContext.setFilterChainResolver(resolver);
  }

  private LaputaFilterChainResolver createFilterChainResolver() {
    if (CollectionUtils.isEmpty(ini)) {
      return null;
    }

    Ini.Section urls = ini.getSection(URLS);
    if (CollectionUtils.isEmpty(urls)) {
      return null;
    }

    LaputaFilterChainResolverFactory factory = new LaputaFilterChainResolverFactory(ini, this.objects);
    return factory.getInstance();
  }
}
