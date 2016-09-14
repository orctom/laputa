package com.orctom.laputa.service;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

/**
 * Created by hao on 4/28/15.
 */
public class DummyServiceTest {

  @Test
  public void test() {
    DummyService service = PowerMockito.mock(DummyService.class);
    PowerMockito.when(service.hello(Mockito.anyString())).thenReturn("hello hao");
    System.out.println(service.hello("mock"));
  }
}
