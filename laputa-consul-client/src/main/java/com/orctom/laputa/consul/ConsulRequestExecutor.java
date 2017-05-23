package com.orctom.laputa.consul;

import com.orctom.laputa.consul.exception.ConsulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class ConsulRequestExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsulRequestExecutor.class);

  private static ExecutorService es = Executors.newFixedThreadPool(2);

  public static <T> T submit(Callable<T> request) {
    Future<T> future = es.submit(request);
    try {
      return future.get(1, TimeUnit.SECONDS);
    } catch (Exception e) {
      throw new ConsulException(e.getMessage(), e);
    }
  }
}
