package com.orctom.laputa.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IdGenerator.class)
public class IdGeneratorTest2 {

  @Test
  public void testS() throws Exception {
    long timestamp = System.currentTimeMillis();
    long delta = 3_600_000;
    final Collection<Long> ids = new ConcurrentLinkedQueue<>();
    IdGenerator spy = PowerMockito.spy(IdGenerator.create());
    for (int i = 0; i < 10_000; i++) {
      when(spy, method(IdGenerator.class, "getCurrentTimestamp"))
          .withNoArguments()
          .thenReturn(timestamp + i * delta + Integer.valueOf(RandomUtils.randomNumeric(6)));
      ids.add(spy.generate());
    }
    final Collection<Long> uniqueIds = new HashSet<>(ids);
    System.out.println(uniqueIds.size() + " vs. " + ids.size());
    assertEquals(uniqueIds.size(), ids.size());
  }
}
