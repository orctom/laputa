package com.orctom.laputa.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

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
