package com.orctom.laputa.utils;

import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class IdGeneratorTest {

  @Test
  public void testGenerator() throws Exception {
    final Collection<Long> ids = new ConcurrentLinkedDeque<>();
    ExecutorService es = Executors.newFixedThreadPool(20);
    final IdGenerator generator = IdGenerator.create();
    for (int i = 0; i < 20; i++) {
      es.submit(() -> {
        try {
          while (!Thread.currentThread().isInterrupted()) {
            ids.add(generator.generate());
          }
        } catch (Exception e) {
          Thread.interrupted();
        }
      });
    }
    es.shutdown();
    es.awaitTermination(3, TimeUnit.SECONDS);
    es.shutdownNow();
    assertEquals(new HashSet<>(ids).size(), ids.size());
  }
}
