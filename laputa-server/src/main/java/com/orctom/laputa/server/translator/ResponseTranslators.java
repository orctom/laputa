package com.orctom.laputa.server.translator;

import com.orctom.laputa.server.config.ServiceConfig;
import com.orctom.laputa.server.model.Accepts;
import com.orctom.laputa.server.model.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ResponseTranslator registry
 * Created by hao on 11/25/15.
 */
public class ResponseTranslators {

  private static final Map<String, ResponseTranslator> ENCODERS = new HashMap<>();

  static {
    add(JsonResponseTranslator.TYPE.getExtension(), new JsonResponseTranslator());
    add(JsonResponseTranslator.TYPE.getValue(), new JsonResponseTranslator());
    add(XmlResponseTranslator.TYPE.getExtension(), new XmlResponseTranslator());
    add(XmlResponseTranslator.TYPE.getValue(), new XmlResponseTranslator());
  }

  public static void add(String type, ResponseTranslator encoder) {
    ENCODERS.put(type, encoder);
  }

  public static ResponseTranslator getTranslator(String uri, String accept) {
    String extension = getExtension(uri);
    ResponseTranslator translator = ENCODERS.get(extension);

    if (null != translator) {
      return translator;
    }

    if (null == accept || 0 == accept.trim().length()) {
      return getResponseTypeEncoder(MediaType.APPLICATION_JSON);
    }

    List<String> accepts = Accepts.sortAsList(accept);

    if (null == accepts) {
      return getResponseTypeEncoder(MediaType.APPLICATION_JSON);
    }
    for (String type : accepts) {
      ResponseTranslator encoder = ENCODERS.get(type);
      if (null != encoder) {
        return encoder;
      }
    }

    return getResponseTypeEncoder(MediaType.APPLICATION_JSON);
  }

  private static String getExtension(String uri) {
    int lastDotIndex = uri.lastIndexOf('.');
    String extension;
    if (lastDotIndex > 1) {
      extension = uri.substring(lastDotIndex);
    } else {
      try {
        extension = ServiceConfig.getInstance().getConfig().getString("default.extension");
      } catch (Exception e) {
        extension = JsonResponseTranslator.TYPE.getExtension();
      }
    }
    return extension;
  }

  private static ResponseTranslator getResponseTypeEncoder(MediaType mediaType) {
    return ENCODERS.get(mediaType.getValue());
  }
}
