package com.orctom.laputa.service.translator.content;

import com.google.common.base.Strings;
import com.orctom.laputa.service.model.Accepts;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.util.PathUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ResponseTranslator registry
 * Created by hao on 11/25/15.
 */
public abstract class ContentTranslators {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentTranslators.class);

  private static final Map<String, ContentTranslator> REGISTRY = new HashMap<>();

  static {
    JsonContentTranslator jsonResponseTranslator = new JsonContentTranslator();
    registerTranslator(JsonContentTranslator.TYPE, jsonResponseTranslator);

    XmlContentTranslator xmlResponseTranslator = new XmlContentTranslator();
    registerTranslator(XmlContentTranslator.TYPE, xmlResponseTranslator);

    ProtoBufContentTranslator protoBufResponseTranslator = new ProtoBufContentTranslator();
    registerTranslator(ProtoBufContentTranslator.TYPE, protoBufResponseTranslator);
  }

  private static void registerTranslator(MediaType mediaType, ContentTranslator contentTranslator) {
    REGISTRY.put(mediaType.getExtension(), contentTranslator);
    REGISTRY.put(mediaType.getValue(), contentTranslator);
  }

  public static void register(ContentTranslator contentTranslator) {
    LOGGER.info("Registered ResponseTranslator: {}} -> {}", contentTranslator.getMediaType(), contentTranslator);
    REGISTRY.put(contentTranslator.getExtension(), contentTranslator);
    REGISTRY.put(contentTranslator.getMediaType(), contentTranslator);
  }

  public static ContentTranslator getTranslator(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    // 1, by extension
    String path = requestWrapper.getPath();
    String extension = PathUtils.getExtension(path);
    if (null != extension) {
      ContentTranslator translator = REGISTRY.get(extension);
      if (null != translator) {
        return translator;
      }
    }

    // 2, if media type is empty
    if (Strings.isNullOrEmpty(responseWrapper.getMediaType())) {
      return new StreamTranslator(responseWrapper.getMediaType(), extension);
    }

    // 3, by accept header
    String accept = requestWrapper.getHeaders().get(HttpHeaderNames.ACCEPT);
    if (Strings.isNullOrEmpty(accept)) {
      return getResponseTranslatorOfType(MediaType.APPLICATION_JSON);
    }

    List<String> accepts = Accepts.sortAsList(accept);
    if (null == accepts) {
      return getResponseTranslatorOfType(MediaType.APPLICATION_JSON);
    }

    for (String type : accepts) {
      ContentTranslator translator = REGISTRY.get(type);
      if (null != translator) {
        return translator;
      }
    }

    return getResponseTranslatorOfType(MediaType.APPLICATION_JSON);
  }

  private static ContentTranslator getResponseTranslatorOfType(MediaType mediaType) {
    return REGISTRY.get(mediaType.getValue());
  }
}
