package com.orctom.laputa.server.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.activation.DataSource;

@Configuration
public class AppConfig {

  @Bean
  public DataSource getDataSource() {
    return null;
  }
}
