package com.orctom.laputa.http.client;

public class HttpClientConfig {

  public static final HttpClientConfig DEFAULT = new HttpClientConfig(0, 8000, false);

  private int threads;
  private int timeout;
  private boolean useNative;

  public HttpClientConfig(int threads, int timeout, boolean useNative) {
    this.threads = threads;
    this.timeout = timeout;
    this.useNative = useNative;
  }

  public int getThreads() {
    return threads;
  }

  public int getTimeout() {
    return timeout;
  }

  public boolean isUseNative() {
    return useNative;
  }
}
