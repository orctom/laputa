package com.orctom.laputa.http.client;

import com.google.common.collect.ImmutableBiMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpClientTest {

//  @Test
  public void test() {
    int size = 10;
    ExecutorService es = Executors.newFixedThreadPool(10);
    List<Long> spans = new ArrayList<>(size);
    CountDownLatch latch = new CountDownLatch(size);
    for (int i = 0; i < size; i++) {
      es.submit(() -> {
        String url = "http://localhost:7000/product/sku.json";
        try {
          long start = System.currentTimeMillis();
          ResponseFuture responseFuture = HttpClient.create().get(url).execute();
          Response response = responseFuture.get();
          long stop = System.currentTimeMillis();
          spans.add((stop - start));
          System.out.println(response.getResponseBody());
          latch.countDown();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
      });
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("=================================================");
    spans.forEach(i -> System.out.print(i + ", "));
    long total = spans.stream().mapToLong(i -> i).sum();
    System.out.println();
    System.out.println("=================================================");
    System.out.println(total / spans.size());
    System.out.println("=================================================");
    System.out.println("done.");
  }

//  @Test
  public void testTimeout() {
//    String url = "http://192.168.4.202:18082/delta.json";
    String url = "http://localhost:7000/product/sku.json";
    try {
      ResponseFuture responseFuture = HttpClient.create().get(url).execute();
      responseFuture.thenAccept(response -> System.out.println(response.getResponseBody()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

//  @Test
  public void testPost() {
    String url = "http://localhost:7000/product/sku/new";
    try {
      ResponseFuture responseFuture = HttpClient.create().post(url).withParams(ImmutableBiMap.of(
          "sku", "sku",
          "desc", "desc",
          "category", "cate",
          "stock", "10"
      )).execute();
      Response response = responseFuture.get();
      System.out.println(response.getResponseBody());
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
