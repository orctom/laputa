package com.orctom.laputa.server.translator;

import com.orctom.laputa.server.config.Configurator;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Encode response data:
 * From Java Object to html/json...
 * Created by hao on 11/25/15.
 */
public interface ResponseTranslator {

  String getMediaType();

  byte[] translate(Object data) throws IOException;

  default byte[] toBytes(String string) {
    Charset charset = Configurator.getInstance().getCharset();
    if (null != charset) {
      return string.getBytes(charset);
    } else {
      return string.getBytes();
    }
  }
}
