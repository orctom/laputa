package com.orctom.laputa.service.model;


/**
 * media types
 * Created by hao on 11/26/15.
 */
public enum MediaType {

  TEXT_HTML(".html", "text/html"),
  APPLICATION_JSON(".json", "application/json"),
  APPLICATION_XML(".xml", "application/xml"),
  PROTO_BUF(".protobuf", "application/protobuf");

  private String extension;
  private String value;

  MediaType(String extension, String value) {
    this.extension = extension;
    this.value = value;
  }

  public String getExtension() {
    return extension;
  }

  public String getValue() {
    return value;
  }
}
