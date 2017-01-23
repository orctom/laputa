package com.orctom.laputa.service.example;

import com.orctom.laputa.service.LaputaService;
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

  public static void main(String[] args) {
      LaputaService.getInstance().run(SampleServer.class);
  }
}
