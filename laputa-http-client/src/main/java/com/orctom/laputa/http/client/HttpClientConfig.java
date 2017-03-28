package com.orctom.laputa.http.client;

public class HttpClientConfig {

  public static final HttpClientConfig DEFAULT = new HttpClientConfig();

  private int threads = 0;
  private int timeout = 7000;
  private boolean useNative = false;

  private HttpClientConfig() {
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HttpClientConfig that = (HttpClientConfig) o;

    if (threads != that.threads) return false;
    if (timeout != that.timeout) return false;
    return useNative == that.useNative;
  }

  @Override
  public int hashCode() {
    int result = threads;
    result = 31 * result + timeout;
    result = 31 * result + (useNative ? 1 : 0);
    return result;
  }
}
