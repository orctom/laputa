package com.orctom.laputa.service.translator.content;

import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Encode response data:
 * From Java Object to html/json...
 * Created by hao on 11/25/15.
 */
public interface ContentTranslator {

  String getMediaType();

  String getExtension();

  byte[] translate(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) throws IOException;

  default byte[] toBytes(String string) {
    Charset charset = Configurator.getInstance().getCharset();
    if (null != charset) {
      return string.getBytes(charset);
    } else {
      return string.getBytes();
    }
  }
}
