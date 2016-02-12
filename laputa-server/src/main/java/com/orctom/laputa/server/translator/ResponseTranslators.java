package com.orctom.laputa.server.translator;

import com.orctom.laputa.server.model.MediaType;
import com.orctom.laputa.server.model.Accepts;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.*;

/**
 * ResponseTranslator registry
 * Created by hao on 11/25/15.
 */
public class ResponseTranslators {

  private static final Map<String, ResponseTranslator> ENCODERS = new HashMap<>();

  static {
    add(JsonResponseTranslator.TYPE.getValue(), new JsonResponseTranslator());
    add(XmlResponseTranslator.TYPE.getValue(), new XmlResponseTranslator());
  }

  public static void add(String type, ResponseTranslator encoder) {
    ENCODERS.put(type, encoder);
  }

  public static ResponseTranslator getTranslator(DefaultHttpRequest request) {
    String uri = request.getUri();
    if (uri.endsWith(".json")) {
      return getResponseTypeEncoder(MediaType.APPLICATION_JSON);
    }
    if (uri.endsWith(".xml")) {
      return getResponseTypeEncoder(MediaType.APPLICATION_XML);
    }
    if (uri.endsWith(".html")) {
      return getResponseTypeEncoder(MediaType.TEXT_HTML);
    }

    String accept = request.headers().get(HttpHeaders.Names.ACCEPT);
    List<String> accepts = Accepts.sortAsList(accept);

    for (String type : accepts) {
      ResponseTranslator encoder = ENCODERS.get(type);
      if (null != encoder) {
        return encoder;
      }
    }

    return getResponseTypeEncoder(MediaType.APPLICATION_JSON);
  }

  private static ResponseTranslator getResponseTypeEncoder(MediaType mediaType) {
    return ENCODERS.get(mediaType.getValue());
  }
}
