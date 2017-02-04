package com.orctom.laputa.utils;

import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class IdGeneratorTest {

  @Test
  public void testSingleThread() {
    final Collection<Long> ids = new ArrayList<>();
    final IdGenerator generator = IdGenerator.create();
    Stopwatch sw = Stopwatch.createStarted();
    for (int i = 0; i < 10_000_000; i++) {
      ids.add(generator.generate());
    }
    sw.stop();
    System.out.println(sw.toString());

    final Collection<Long> uniqueIds = new HashSet<>(ids);
    System.out.println(uniqueIds.size() + " vs. " + ids.size());
    assertEquals(uniqueIds.size(), ids.size());
    System.out.println("rate: " + ids.size() / sw.elapsed(TimeUnit.MILLISECONDS) + "/ms");
  }

  @Test
  public void testMultiThreads() throws Exception {
    final Collection<Long> ids = new ConcurrentLinkedQueue<>();
    ExecutorService es = Executors.newFixedThreadPool(20);
    final IdGenerator generator = IdGenerator.create();
    CountDownLatch latch = new CountDownLatch(1);
    Stopwatch sw = Stopwatch.createUnstarted();
    for (int i = 0; i < 20; i++) {
      es.submit(() -> {
        try {
          latch.await();
          sw.start();
          while (!Thread.currentThread().isInterrupted()) {
            ids.add(generator.generate());
          }
        } catch (Exception e) {
          Thread.interrupted();
        }
      });
    }
    latch.countDown();
    es.shutdown();
    es.awaitTermination(3, TimeUnit.SECONDS);
    es.shutdownNow();

    sw.stop();
    System.out.println(sw.toString());

    final Collection<Long> uniqueIds = new HashSet<>(ids);
    System.out.println(uniqueIds.size() + " vs. " + ids.size());
    assertEquals(uniqueIds.size(), ids.size());
    System.out.println("rate: " + ids.size() / sw.elapsed(TimeUnit.MILLISECONDS) + "/ms");
  }

  @Test
  public void testMultiHostIds() throws Exception {
    final Collection<Long> ids = new ConcurrentLinkedQueue<>();
    ExecutorService es = Executors.newFixedThreadPool(20);
    CountDownLatch latch = new CountDownLatch(1);
    Stopwatch sw = Stopwatch.createUnstarted();
    for (int i = 0; i < 20; i++) {
      final IdGenerator generator = IdGenerator.create(i);
      es.submit(() -> {
        try {
          latch.await();
          sw.start();
          while (!Thread.currentThread().isInterrupted()) {
            ids.add(generator.generate());
          }
        } catch (Exception e) {
          Thread.interrupted();
        }
      });
    }
    latch.countDown();
    es.shutdown();
    es.awaitTermination(3, TimeUnit.SECONDS);
    es.shutdownNow();

    sw.stop();
    System.out.println(sw.toString());

    final Collection<Long> uniqueIds = new HashSet<>(ids);
    System.out.println(uniqueIds.size() + " vs. " + ids.size());
    assertEquals(uniqueIds.size(), ids.size());
    System.out.println("rate: " + ids.size() / sw.elapsed(TimeUnit.MILLISECONDS) + "/ms");
  }
}
