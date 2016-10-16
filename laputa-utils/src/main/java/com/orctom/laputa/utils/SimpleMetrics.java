package com.orctom.laputa.utils;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple metrics counter
 * Created by hao on 8/7/16.
 */
public class SimpleMetrics {

  private final Logger logger;
  private final long period;
  private final TimeUnit unit;
  private Map<String, SimpleMetricsMeter> meters;
  private Map<String, Callable<Integer>> gauges = new HashMap<>();
  private ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();

  private SimpleMetrics(Logger logger, long period, TimeUnit unit) {
    this.logger = logger;
    this.period = period;
    this.unit = unit;
    resetMeters();
    startReporter();
  }

  public static SimpleMetrics create(Logger logger) {
    return new SimpleMetrics(logger, 30, TimeUnit.SECONDS);
  }

  public static SimpleMetrics create(Logger logger, long period, TimeUnit unit) {
    return new SimpleMetrics(logger, period, unit);
  }

  public void resetMeters() {
    meters = new HashMap<>();
  }

  private void startReporter() {
    es.scheduleAtFixedRate(this::report, period, period, unit);
  }

  public void shutdown() {
    try {
      unit.sleep(period);
    } catch (InterruptedException ignored) {
    }
    es.shutdown();

    meters = null;
    gauges = null;
  }

  public void mark(String key) {
    SimpleMetricsMeter meter = meter(key);
    meter.increase();
  }

  public SimpleMetricsMeter meter(String key) {
    SimpleMetricsMeter meter = meters.get(key);
    if (null != meter) {
      return meter;
    }

    meter = new SimpleMetricsMeter(0);
    synchronized (this) {
      SimpleMetricsMeter old = meters.put(key, meter);
      if (null != old) {
        meter.increaseBy(old.getValue());
      }
    }
    return meter;
  }

  public void gauge(String key, Callable<Integer> callable) {
    gauges.put(key, callable);
  }

  private void report() {
    reportGauges();
    reportMeters();
  }

  private void reportGauges() {
    for (Map.Entry<String, Callable<Integer>> entry : gauges.entrySet()) {
      try {
        int value = entry.getValue().call();
        logger.info("gauge: {}, value: {}", entry.getKey(), value);
      } catch (Exception e) {
        logger.error("failed to collect gauge: {}, due tu {}", entry.getKey(), e.getMessage());
      }
    }
  }

  private void reportMeters() {
    double duration = unit.toSeconds(period);
    for (Map.Entry<String, SimpleMetricsMeter> entry : meters.entrySet()) {
      SimpleMetricsMeter value = entry.getValue();
      int count = value.getAndSet(0);
      logger.info("meter: {}, count: {}, mean: {}/s", entry.getKey(), count, count / duration);
    }
  }
}
