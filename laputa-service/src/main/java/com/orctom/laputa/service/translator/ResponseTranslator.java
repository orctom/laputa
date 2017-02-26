package com.orctom.laputa.service.translator;

import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestMapping;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Encode response data:
 * From Java Object to html/json...
 * Created by hao on 11/25/15.
 */
public interface ResponseTranslator {

  String getMediaType();

  byte[] translate(RequestMapping mapping, Object data, Context ctx) throws IOException;

  default byte[] toBytes(String string) {
    Charset charset = Configurator.getInstance().getCharset();
    if (null != charset) {
      return string.getBytes(charset);
    } else {
      return string.getBytes();
    }
  }
}
