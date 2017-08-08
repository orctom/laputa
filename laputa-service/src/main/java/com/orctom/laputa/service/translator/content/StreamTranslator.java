package com.orctom.laputa.service.translator.content;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteStreams;
import com.orctom.laputa.service.exception.PathNotFoundException;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class StreamTranslator implements ContentTranslator {

  private static LoadingCache<File, byte[]> fileCache = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .build(
          new CacheLoader<File, byte[]>() {
            public byte[] load(File file) throws IOException {
              return ByteStreams.toByteArray(new FileInputStream(file));
            }
          });

  private String mediaType;
  private String extension;

  StreamTranslator(String mimeType, String extension) {
    this.mediaType = mimeType;
    this.extension = extension;
  }

  @Override
  public String getMediaType() {
    return mediaType;
  }

  @Override
  public String getExtension() {
    return extension;
  }

  @Override
  public byte[] translate(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) throws IOException {
    Object result = responseWrapper.getResult();
    if (result instanceof byte[]) {
      return (byte[]) result;
    }

    if (result instanceof File) {
      File file = (File) result;
      try {
        return fileCache.get(file);
      } catch (ExecutionException e) {
        throw new PathNotFoundException(e.getMessage(), e);
      }
    }

    return responseWrapper.getResult().toString().getBytes();
  }
}
