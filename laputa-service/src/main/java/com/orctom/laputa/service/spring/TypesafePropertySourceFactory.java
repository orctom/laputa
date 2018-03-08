package com.orctom.laputa.service.spring;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

public class TypesafePropertySourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
    Config config = ConfigFactory.load(resource.getResource().getFilename()).resolve();

    String safeName = name == null ? "typeSafe" : name;
    return new TypesafeConfigPropertySource(safeName, config);
  }

}
