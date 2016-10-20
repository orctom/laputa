package com.orctom.laputa.service.model;

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

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public void setFile(String file) {
    this.file = new File(file);
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public void setOriginalFilename(String originalFilename) {
    this.originalFilename = originalFilename;
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
