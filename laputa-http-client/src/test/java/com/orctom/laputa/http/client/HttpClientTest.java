package com.orctom.laputa.http.client;

import io.netty.handler.codec.http.HttpMethod;
import org.junit.Test;

import java.net.URI;

public class HttpClientTest {

  @Test
  public void test() {
    try {
      HttpClient httpClient = HttpClient.get(HttpClientConfig.DEFAULT);
      String url = "http://192.168.4.202:18081/app_api/wifiShare/campaign.html?id=campaign-name";
//      String url = "http://localhost:7000/product/sku.json";
//      String url = "http://localhost:7000/product/hello.json?hello=world";
      ResponseFuture responseFuture = httpClient.request(HttpMethod.GET, URI.create(url), null, null);
      Response response = responseFuture.get();
      System.out.println(new String(response.getContent()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
