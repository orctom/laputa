package com.orctom.laputa.service.shiro.env;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.config.IniFactorySupport;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.env.DefaultEnvironment;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Initializable;

import java.util.Map;

public class LaputaEnvironment extends DefaultEnvironment implements Initializable {

  @Override
  public void init() throws ShiroException {
    this.objects.clear();
    IniSecurityManagerFactory factory = new IniSecurityManagerFactory(IniFactorySupport.DEFAULT_INI_RESOURCE_PATH);

    Map<String, ?> beans = factory.getBeans();
    if (!CollectionUtils.isEmpty(beans)) {
      this.objects.putAll(beans);
    }

    SecurityManager securityManager = factory.getInstance();
    super.setSecurityManager(securityManager);
    SecurityUtils.setSecurityManager(securityManager);
  }
}
