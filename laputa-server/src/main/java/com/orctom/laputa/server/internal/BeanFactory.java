package com.orctom.laputa.server.internal;

import java.util.Collection;

public interface BeanFactory {

  public <T> T getInstance(Class<T> type);

  public <T> Collection<T> getInstances(Class<T> type);
}
