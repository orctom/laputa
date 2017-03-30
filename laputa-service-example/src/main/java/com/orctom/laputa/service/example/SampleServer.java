package com.orctom.laputa.service.example;

import com.orctom.laputa.service.LaputaService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.activation.DataSource;

@Configuration
@ComponentScan({
    "com.orctom.laputa.service.example"
})
public class SampleServer {

  @Bean
  public DataSource getDataSource() {
    return null;
  }

  private static void initShiro() {
    Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
    SecurityManager securityManager = factory.getInstance();
    SecurityUtils.setSecurityManager(securityManager);
  }

  public static void main(String[] args) {
    LaputaService.getInstance().run(SampleServer.class);
    initShiro();
  }
}
