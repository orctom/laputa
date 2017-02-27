package com.orctom.laputa.service.translator;

import com.google.common.base.Strings;
import com.orctom.laputa.service.model.Accepts;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.RequestWrapper;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ResponseTranslator registry
 * Created by hao on 11/25/15.
 */
public abstract class ResponseTranslators {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseTranslators.class);

  private static final Map<String, ResponseTranslator> REGISTRY = new HashMap<>();

  static {
    JsonResponseTranslator jsonResponseTranslator = new JsonResponseTranslator();
    registerTranslator(JsonResponseTranslator.TYPE, jsonResponseTranslator);

    XmlResponseTranslator xmlResponseTranslator = new XmlResponseTranslator();
    registerTranslator(XmlResponseTranslator.TYPE, xmlResponseTranslator);

    ProtoBufResponseTranslator protoBufResponseTranslator = new ProtoBufResponseTranslator();
    registerTranslator(ProtoBufResponseTranslator.TYPE, protoBufResponseTranslator);
  }

  private static void registerTranslator(MediaType mediaType, ResponseTranslator encoder) {
    REGISTRY.put(mediaType.getExtension(), encoder);
    REGISTRY.put(mediaType.getValue(), encoder);
  }

  public static void register(ResponseTranslator responseTranslator) {
    LOGGER.info("Registered ResponseTranslator: {}} -> {}", responseTranslator.getMediaType(), responseTranslator);
    registerTranslator(responseTranslator.getMediaType(), responseTranslator);
  }

  public static ResponseTranslator getTranslator(RequestWrapper requestWrapper, String accept) {
    // 1, by extension
    String uri = requestWrapper.getPath();
    String extension = getExtension(uri);
    if (null != extension) {
      ResponseTranslator translator = REGISTRY.get(extension);
      if (null != translator) {
        return translator;
      }
    }

    // 2, by accept header
    if (Strings.isNullOrEmpty(accept)) {
      return getResponseTranslatorOfType(MediaType.APPLICATION_JSON);
    }

    List<String> accepts = Accepts.sortAsList(accept);
    if (null == accepts) {
      return getResponseTranslatorOfType(MediaType.APPLICATION_JSON);
    }

    for (String type : accepts) {
      ResponseTranslator encoder = REGISTRY.get(type);
      if (null != encoder) {
        return encoder;
      }
    }

    return getResponseTranslatorOfType(MediaType.APPLICATION_JSON);
  }

  private static String getExtension(String uri) {
    int lastDotIndex = uri.lastIndexOf('.');
    if (lastDotIndex > 1 && lastDotIndex < uri.length() - 1) {
      return uri.substring(lastDotIndex);
    } else {
      return null;
    }
  }

  private static ResponseTranslator getResponseTranslatorOfType(MediaType mediaType) {
    return REGISTRY.get(mediaType.getValue());
  }
}
