package com.orctom.laputa.service.model;

/**
 * Response with payload
 */
public class DataResponse<T> extends Response {

  private T data;

  public DataResponse(T data) {
    this.data = data;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "DataResponse{" +
        "data=" + data +
        '}';
  }
}
