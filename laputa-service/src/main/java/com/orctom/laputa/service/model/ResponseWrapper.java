package com.orctom.laputa.service.model;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Encoded response
 * Created by hao on 11/30/15.
 */
public class ResponseWrapper {

  private String mediaType;
  private byte[] content;
  private File file;
  private HttpResponseStatus status = OK;
  private String redirectTo;
  private boolean permanentRedirect;
  private Map<String, Object> data = new HashMap<>();
  private Set<ResponseCookie> cookies = new HashSet<>();

  public ResponseWrapper(String mediaType) {
    this.mediaType = mediaType;
  }

  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public HttpResponseStatus getStatus() {
    return status;
  }

  public void setStatus(HttpResponseStatus status) {
    this.status = status;
  }

  public String getRedirectTo() {
    return redirectTo;
  }

  public void setRedirectTo(String redirectTo) {
    this.redirectTo = redirectTo;
  }

  public boolean isPermanentRedirect() {
    return permanentRedirect;
  }

  public void setPermanentRedirect(boolean permanentRedirect) {
    this.permanentRedirect = permanentRedirect;
  }

  public void setData(String key, Object value) {
    data.put(key, value);
  }

  public Map<String, Object> getData() {
    return data;
  }

  public Set<ResponseCookie> getCookies() {
    return cookies;
  }

  public void setCookies(Set<ResponseCookie> cookies) {
    this.cookies = cookies;
  }

  public void permanentRedirectTo(String permanentRedirectTo) {
    this.redirectTo = permanentRedirectTo;
    this.permanentRedirect = true;
  }

  public void setCookie(String name, String value) {
    this.cookies.add(new ResponseCookie(name, value));
  }

  public void setCookie(String name, String value, long maxAge, boolean secure, boolean httpOnly) {
    this.cookies.add(new ResponseCookie(name, value, maxAge, secure, httpOnly));
  }

  public void setCookie(String name, String value, long maxAge, boolean secure, boolean httpOnly, String domain) {
    ResponseCookie cookie = new ResponseCookie(name, value, maxAge, secure, httpOnly);
    cookie.setDomain(domain);
    this.cookies.add(cookie);
  }

  public boolean hasContent() {
    return OK != status || null != redirectTo || null != content || null != file;
  }
}
