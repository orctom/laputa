package com.orctom.laputa.service.model;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Encoded response
 * Created by hao on 11/30/15.
 */
public class ResponseWrapper {

  private String mediaType;
  private byte[] content;
  private HttpResponseStatus status = HttpResponseStatus.OK;
  private String redirectTo;
  private boolean permanentRedirect;

  public ResponseWrapper(String mediaType, byte[] content) {
    this.content = content;
    this.mediaType = mediaType;
  }

  public ResponseWrapper(String mediaType, byte[] content, HttpResponseStatus status) {
    this.content = content;
    this.mediaType = mediaType;
    this.status = status;
  }

  public ResponseWrapper(String redirectTo, boolean permanentRedirect) {
    this.redirectTo = redirectTo;
    this.permanentRedirect = permanentRedirect;
  }

  public byte[] getContent() {
    return content;
  }

  public String getMediaType() {
    return mediaType;
  }

  public HttpResponseStatus getStatus() {
    return status;
  }

  public String getRedirectTo() {
    return redirectTo;
  }

  public boolean isPermanentRedirect() {
    return permanentRedirect;
  }
}
