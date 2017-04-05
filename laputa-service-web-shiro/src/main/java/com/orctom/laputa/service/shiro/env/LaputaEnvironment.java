package com.orctom.laputa.service.shiro.env;

import com.orctom.laputa.service.shiro.config.LaputaIniSecurityManagerFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.env.DefaultEnvironment;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Initializable;

import java.util.Map;

public class LaputaEnvironment extends DefaultEnvironment implements Initializable {

  @Override
  public void init() throws ShiroException {
    this.objects.clear();
    LaputaIniSecurityManagerFactory factory = new LaputaIniSecurityManagerFactory();

    SecurityManager securityManager = factory.getInstance();
    super.setSecurityManager(securityManager);

    Map<String, ?> beans = factory.getBeans();
    if (!CollectionUtils.isEmpty(beans)) {
      this.objects.putAll(beans);
    }

    SecurityUtils.setSecurityManager(securityManager);
  }
}
