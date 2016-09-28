package com.orctom.laputa.server.model;

import java.io.File;
import java.util.Objects;

/**
 * Wrapper for uploaded file
 * Created by hao on 9/28/16.
 */
public class MultipartFile {

  private File file;
  private String contentType;
  private String originalFilename;

  public MultipartFile(String contentType, File file, String originalFilename) {
    this.contentType = contentType;
    this.file = file;
    this.originalFilename = originalFilename;
  }

  public String getContentType() {
    return contentType;
  }

  public File getFile() {
    return file;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MultipartFile)) return false;
    MultipartFile that = (MultipartFile) o;
    return Objects.equals(file, that.file) &&
        Objects.equals(contentType, that.contentType) &&
        Objects.equals(originalFilename, that.originalFilename);
  }

  @Override
  public int hashCode() {
    return Objects.hash(file, contentType, originalFilename);
  }

  @Override
  public String toString() {
    return "MultipartFile{" +
        "contentType='" + contentType + '\'' +
        ", file=" + file +
        ", originalFilename='" + originalFilename + '\'' +
        '}';
  }
}
