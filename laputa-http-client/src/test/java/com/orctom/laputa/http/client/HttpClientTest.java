package com.orctom.laputa.http.client;

import org.junit.Test;

import java.net.URI;

public class HttpClientTest {

  @Test
  public void test() {
    try(HttpClient httpClient = new HttpClient()) {
      String url = "http://192.168.4.202:18081/app_api/wifiShare/campaign.html?id=campaign-name";
      try {
        httpClient.request(URI.create(url));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
