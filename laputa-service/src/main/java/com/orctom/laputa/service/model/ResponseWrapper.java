package com.orctom.laputa.service.model;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.RandomAccessFile;

/**
 * Encoded response
 * Created by hao on 11/30/15.
 */
public class ResponseWrapper {

  private String mediaType;
  private byte[] content;
  private RandomAccessFile file;
  private HttpResponseStatus status = HttpResponseStatus.OK;
  private String redirectTo;
  private boolean permanentRedirect;

  public ResponseWrapper(String mediaType, byte[] content) {
    this.content = content;
    this.mediaType = mediaType;
  }

  public ResponseWrapper(String mediaType, HttpResponseStatus status) {
    this.mediaType = mediaType;
    this.status = status;
  }

  public ResponseWrapper(String mediaType, RandomAccessFile file) {
    this.mediaType = mediaType;
    this.file = file;
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

  public RandomAccessFile getFile() {
    return file;
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
