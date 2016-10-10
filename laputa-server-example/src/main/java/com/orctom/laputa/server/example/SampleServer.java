package com.orctom.laputa.server.example;

import com.orctom.laputa.server.LaputaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.activation.DataSource;

@Configuration
@ComponentScan({
    "com.orctom.laputa.server.example"
})
public class SampleServer {

  @Bean
  public DataSource getDataSource() {
    return null;
  }

  public static void main(String[] args) {
    try {
      LaputaService.getInstance().run(SampleServer.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
