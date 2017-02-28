package com.orctom.laputa.service.example;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DummyStressTestClient {

  @Test
  public void get_request_returns200OK() throws Exception {
    Runnable task = () -> {
      for (int i = 0; i < 1_000_000; i++) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
          HttpGet httpGet = new HttpGet("http://localhost:7000/product/sku.json");

          try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            System.out.println(response1.getStatusLine());
            HttpEntity entity = response1.getEntity();
            System.out.println(EntityUtils.toString(entity));
          } catch (Exception e) {
            e.printStackTrace();
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          try {
            httpclient.close();
          } catch (IOException ignored) {
          }
        }
      }
    };

    ExecutorService es = Executors.newFixedThreadPool(40);
    for (int i = 0; i < 40; i++) {
      es.submit(task);
    }
    es.shutdown();
    es.awaitTermination(10, TimeUnit.MINUTES);
    es.shutdownNow();
  }
}
