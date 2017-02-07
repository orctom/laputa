package com.orctom.laputa.utils;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThat;

public class RandomUtilsTest {

  private static final int COUNTS = 1000000;

  @Test
  public void testRandomAlpha() {
    Stopwatch sw = Stopwatch.createStarted();
    Set<String> randoms = new HashSet<>();
    for (int i = 0; i < COUNTS; i++) {
      randoms.add(RandomUtils.randomAlpha(10));
    }
    sw.stop();
    System.out.println("randomAlpha                          = " + sw.toString() + "\t\t" + randoms.size());

    Stopwatch sw2 = Stopwatch.createStarted();
    Set<String> randoms2 = new HashSet<>();
    for (int i = 0; i < COUNTS; i++) {
      randoms2.add(RandomStringUtils.randomAlphabetic(10));
    }
    sw2.stop();
    System.out.println("RandomStringUtils.randomAlphabetic   = " + sw2.toString() + "\t\t" + randoms2.size());

    Assert.assertTrue(sw.elapsed(TimeUnit.SECONDS) <= sw2.elapsed(TimeUnit.SECONDS));
    Assert.assertTrue(randoms.size() >= randoms2.size() * .9999);
  }

  @Test
  public void testRandomNumeric() {
    Stopwatch sw = Stopwatch.createStarted();
    Set<String> randoms = new HashSet<>();
    for (int i = 0; i < COUNTS; i++) {
      randoms.add(RandomUtils.randomNumeric(10));
    }
    sw.stop();
    System.out.println("randomNumeric                        = " + sw.toString() + "\t\t" + randoms.size());

    Stopwatch sw2 = Stopwatch.createStarted();
    Set<String> randoms2 = new HashSet<>();
    for (int i = 0; i < COUNTS; i++) {
      randoms2.add(RandomStringUtils.randomNumeric(10));
    }
    sw2.stop();
    System.out.println("RandomStringUtils.randomNumeric      = " + sw2.toString() + "\t\t" + randoms2.size());

    Assert.assertTrue(sw.elapsed(TimeUnit.SECONDS) <= sw2.elapsed(TimeUnit.SECONDS));
    Assert.assertTrue(randoms.size() >= randoms2.size() * .9999);
  }

  @Test
  public void testRandomAlphaNumeric() {
    Stopwatch sw = Stopwatch.createStarted();
    Set<String> randoms = new HashSet<>();
    for (int i = 0; i < COUNTS; i++) {
      randoms.add(RandomUtils.randomAlphaNumeric(10));
    }
    sw.stop();
    System.out.println("randomAlphaNumeric                   = " + sw.toString() + "\t\t" + randoms.size());

    Stopwatch sw2 = Stopwatch.createStarted();
    Set<String> randoms2 = new HashSet<>();
    for (int i = 0; i < COUNTS; i++) {
      randoms2.add(RandomStringUtils.randomAlphanumeric(10));
    }
    sw2.stop();
    System.out.println("RandomStringUtils.randomAlphanumeric = " + sw2.toString() + "\t\t" + randoms2.size());

    Assert.assertTrue(sw.elapsed(TimeUnit.SECONDS) <= sw2.elapsed(TimeUnit.SECONDS));
    Assert.assertTrue(randoms.size() >= randoms2.size() * .9999);
  }

  @Test
  public void testNextLong() {
    long start = 100;
    long end = 150;
    for (int i = 0; i < 100; i++) {
      long random = RandomUtils.nextLong(start, end);
      assertThat(random, Matchers.greaterThanOrEqualTo(start));
      assertThat(random, Matchers.lessThan(end));
    }
  }
}
