package com.orctom.laputa.service.model;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Encoded response
 * Created by hao on 11/30/15.
 */
public class ResponseWrapper {

  private String mediaType;
  private Object result;
  private byte[] content;
  private String template;
  private File file;
  private HttpResponseStatus status = OK;
  private boolean permanentRedirect;
  private Messenger messenger = new Messenger();

  public ResponseWrapper(String mediaType) {
    this.mediaType = mediaType;
  }

  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
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
    return messenger.getRedirectTo();
  }

  public void setRedirectTo(String redirectTo) {
    messenger.setRedirectTo(redirectTo);
  }

  public boolean isPermanentRedirect() {
    return permanentRedirect;
  }

  public void setPermanentRedirect(boolean permanentRedirect) {
    this.permanentRedirect = permanentRedirect;
  }

  public Messenger getMessenger() {
    return messenger;
  }

  public void setData(String key, Object value) {
    messenger.setData(key, value);
  }

  public Map<String, Object> getData() {
    return messenger.getData();
  }

  public Set<ResponseCookie> getCookies() {
    return messenger.getCookies();
  }

  public void setCookies(Set<ResponseCookie> cookies) {
    messenger.addCookie(cookies);
  }

  public void permanentRedirectTo(String redirectTo) {
    messenger.setRedirectTo(redirectTo);
    this.permanentRedirect = true;
  }

  public void setCookie(String name, String value, long maxAge, boolean secure, boolean httpOnly, String domain) {
    messenger.setCookie(name, value, maxAge, secure, httpOnly, domain);
  }

  public boolean hasContent() {
    return null != result || OK != status || null != messenger.getRedirectTo() || null != content || null != file;
  }
}
