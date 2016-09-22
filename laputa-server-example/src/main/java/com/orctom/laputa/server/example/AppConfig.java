package com.orctom.laputa.server.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.activation.DataSource;

@Configuration
@ComponentScan({
    "com.orctom.laputa.server.example"
})
public class AppConfig {

  @Bean
  public DataSource getDataSource() {
    return null;
  }
}
