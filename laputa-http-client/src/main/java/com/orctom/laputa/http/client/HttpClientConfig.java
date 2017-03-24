package com.orctom.laputa.http.client;

public class HttpClientConfig {

  public static final HttpClientConfig DEFAULT = new HttpClientConfig(0, 8000);

  private int threads;
  private int timeout;

  public HttpClientConfig(int threads, int timeout) {
    this.threads = threads;
    this.timeout = timeout;
  }

  public int getThreads() {
    return threads;
  }

  public int getTimeout() {
    return timeout;
  }
}
