package com.orctom.laputa.service.autoconfigure;

import com.orctom.laputa.service.spring.TypesafeConfigPropertySource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

@Configuration
public class LaputaAutoConfiguration {


  @Bean
  public TypesafeConfigPropertySource provideTypesafeConfigPropertySource(ConfigurableEnvironment env) {
    Config conf = ConfigFactory.load().resolve();
    TypesafeConfigPropertySource source = new TypesafeConfigPropertySource("typeSafe", conf);
    MutablePropertySources sources = env.getPropertySources();
    sources.addFirst(source); // Choose if you want it first or last
    return source;
  }
}
