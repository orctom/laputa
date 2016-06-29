package com.orctom.laputa.server.internal;

import java.util.List;

public interface BeanFactory {

  public <T> T getInstance(Class<T> type);

  public <T> List<T> getInstances(Class<T> type);
}
