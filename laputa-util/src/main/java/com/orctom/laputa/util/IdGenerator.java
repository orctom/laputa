package com.orctom.laputa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Database ID generator
 * Created by chenhao on 7/14/16.
 */
public class IdGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerator.class);

  private static final long EPOCH = 1467302400000L;
  private static final long SEQUENCE_BITS = 12L;
  private static final long HOST_ID_BITS = 5L;
  private static final long HOST_ID_SHIFT = SEQUENCE_BITS;
  private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + HOST_ID_BITS;
  private static final long SEQUENCE_MAST = ~(-1L << SEQUENCE_BITS);
  private static final long HOST_ID_MAX = ~(-1L << HOST_ID_BITS);

  private long hostId;
  private long lastTimestamp = -1L;
  private long sequence = 0L;

  private IdGenerator(long hostId) {
    LOGGER.info("hostId: " + hostId);
    this.hostId = hostId;
  }

  /**
   * For non-cluster usage
   */
  public static IdGenerator create() {
    return new IdGenerator(getHostId());
  }

  /**
   * For <code>cluster</code> environment, a unique hostId must be provided for each node in the cluster.
   *
   * @param hostId <code>0 ~ 31</code>, a.k.a. supports 32 nodes' cluster maximum.
   */
  public static IdGenerator create(long hostId) {
    if (hostId > HOST_ID_MAX || hostId < 0) {
      throw new IllegalArgumentException("Host ID must be positive and less than " + HOST_ID_MAX);
    }
    return new IdGenerator(hostId);
  }

  public synchronized long generate() {
    long currentTimestamp = getCurrentTimestamp();
    if (lastTimestamp == currentTimestamp) {
      sequence = (sequence + 1) & SEQUENCE_MAST;
      if (sequence == 0) {
        currentTimestamp = tillNextMilli(lastTimestamp);
      }
    } else {
      sequence = 0;
    }

    lastTimestamp = currentTimestamp;
    return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT) | hostId << HOST_ID_SHIFT | sequence;
  }

  private long tillNextMilli(final long lastTimestamp) {
    long timestamp = getCurrentTimestamp();
    while (timestamp <= lastTimestamp) {
      timestamp = getCurrentTimestamp();
    }
    return timestamp;
  }

  private long getCurrentTimestamp() {
    return System.currentTimeMillis();
  }

  private static long getHostId() {
    Long id = HostUtils.getHostId();
    if (null == id) {
      return new Random().nextLong() % HOST_ID_MAX;
    }
    return id % HOST_ID_MAX;
  }
}
