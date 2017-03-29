package com.orctom.laputa.service.model;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;
import java.util.Set;

/**
 * Encoded response
 * Created by hao on 11/30/15.
 */
public class ResponseWrapper {

  private String mediaType;
  private byte[] content;
  private File file;
  private HttpResponseStatus status = HttpResponseStatus.OK;
  private String redirectTo;
  private boolean permanentRedirect;
  private Set<ResponseCookie> cookies;

  public ResponseWrapper(String mediaType, byte[] content) {
    this.content = content;
    this.mediaType = mediaType;
  }

  public ResponseWrapper(String mediaType, HttpResponseStatus status) {
    this.mediaType = mediaType;
    this.status = status;
  }

  public ResponseWrapper(String mediaType, File file) {
    this.mediaType = mediaType;
    this.file = file;
  }

  public ResponseWrapper(String mediaType, byte[] content, HttpResponseStatus status) {
    this.content = content;
    this.mediaType = mediaType;
    this.status = status;
  }

  public ResponseWrapper(String mediaType, byte[] content, HttpResponseStatus status, Set<ResponseCookie> cookies) {
    this.content = content;
    this.mediaType = mediaType;
    this.status = status;
    this.cookies = cookies;
  }

  public ResponseWrapper(String redirectTo, boolean permanentRedirect) {
    this.redirectTo = redirectTo;
    this.permanentRedirect = permanentRedirect;
  }

  public ResponseWrapper(String redirectTo, boolean permanentRedirect, Set<ResponseCookie> cookies) {
    this.redirectTo = redirectTo;
    this.permanentRedirect = permanentRedirect;
    this.cookies = cookies;
  }

  public byte[] getContent() {
    return content;
  }

  public String getMediaType() {
    return mediaType;
  }

  public File getFile() {
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

  public Set<ResponseCookie> getCookies() {
    return cookies;
  }
}
