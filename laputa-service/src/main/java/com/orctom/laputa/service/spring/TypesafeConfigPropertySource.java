package com.orctom.laputa.service.spring;

import com.typesafe.config.Config;
import org.springframework.core.env.PropertySource;

public class TypesafeConfigPropertySource extends PropertySource<Config> {
  public TypesafeConfigPropertySource(String name, Config source) {
    super(name, source);
  }

  @Override
  public Object getProperty(String path) {
    if (source.hasPath(path)) {
      return source.getAnyRef(path);
    }
    return null;
  }
}
