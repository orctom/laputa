package com.orctom.laputa.http.client.core;

import java.util.concurrent.ThreadFactory;

public class HttpClientThreadFactory implements ThreadFactory {

  private int counter = 0;

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r, "HttpClient#" + ++counter);
    t.setDaemon(true);
    return t;
  }
}
