package com.orctom.laputa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileUtils {

  public static long copy(final InputStream input, final OutputStream output) throws IOException {
    return copy(input, output, 8192);
  }

  public static long copy(final InputStream input, final OutputStream output, int buffersize) throws IOException {
    final byte[] buffer = new byte[buffersize];
    int n = 0;
    long count=0;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }
}
