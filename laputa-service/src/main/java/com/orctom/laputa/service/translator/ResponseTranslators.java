package com.orctom.laputa.service.translator;

import com.orctom.laputa.service.config.Configurator;
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
    registerTranslator(JsonResponseTranslator.TYPE.getExtension(), jsonResponseTranslator);
    registerTranslator(JsonResponseTranslator.TYPE.getValue(), jsonResponseTranslator);

    XmlResponseTranslator xmlResponseTranslator = new XmlResponseTranslator();
    registerTranslator(XmlResponseTranslator.TYPE.getExtension(), xmlResponseTranslator);
    registerTranslator(XmlResponseTranslator.TYPE.getValue(), xmlResponseTranslator);

    ProtoBufResponseTranslator protoBufResponseTranslator = new ProtoBufResponseTranslator();
    registerTranslator(ProtoBufResponseTranslator.TYPE.getExtension(), protoBufResponseTranslator);
    registerTranslator(ProtoBufResponseTranslator.TYPE.getValue(), protoBufResponseTranslator);
  }

  private static void registerTranslator(String type, ResponseTranslator encoder) {
    REGISTRY.put(type, encoder);
  }

  public static void register(ResponseTranslator responseTranslator) {
    LOGGER.info("Registered ResponseTranslator: {}} -> {}", responseTranslator.getMediaType(), responseTranslator);
    registerTranslator(responseTranslator.getMediaType(), responseTranslator);
  }

  public static ResponseTranslator getTranslator(RequestWrapper requestWrapper, String accept) {
    String uri = requestWrapper.getPath();
    String extension = getExtension(uri);
    ResponseTranslator translator = REGISTRY.get(extension);

    if (null != translator) {
      if (isRequestingForWebButNotGetMethod(requestWrapper, translator)) {
        return getResponseTypeEncoder(MediaType.APPLICATION_JSON);
      }
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
      ResponseTranslator encoder = REGISTRY.get(type);
      if (null != encoder) {
        if (isRequestingForWebButNotGetMethod(requestWrapper, encoder)) {
          return getResponseTypeEncoder(MediaType.APPLICATION_JSON);
        }
        return encoder;
      }
    }

    return getResponseTypeEncoder(MediaType.APPLICATION_JSON);
  }

  private static boolean isRequestingForWebButNotGetMethod(RequestWrapper requestWrapper, ResponseTranslator translator) {
    return translator instanceof TemplateResponseTranslator && HttpMethod.GET != requestWrapper.getHttpMethod();
  }

  private static String getExtension(String uri) {
    int lastDotIndex = uri.lastIndexOf('.');
    String extension;
    if (lastDotIndex > 1) {
      extension = uri.substring(lastDotIndex);
    } else {
      try {
        extension = Configurator.getInstance().getConfig().getString("default.extension");
      } catch (Exception e) {
        extension = JsonResponseTranslator.TYPE.getExtension();
      }
    }
    return extension;
  }

  private static ResponseTranslator getResponseTypeEncoder(MediaType mediaType) {
    return REGISTRY.get(mediaType.getValue());
  }
}
