package com.orctom.laputa.service.translator.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class ResponseTranslators {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseTranslators.class);

  private static final List<ResponseTranslator> REGISTRY = new ArrayList<>();

  static {
    REGISTRY.add(new ErrorResponseTranslator());
    REGISTRY.add(new RedirectResponseTranslator());
    REGISTRY.add(new ContentResponseTranslator());
  }

  public synchronized static void register(ResponseTranslator translator) {
    REGISTRY.add(REGISTRY.size() - 1, translator);
    LOGGER.info("Registered ResponseTranslator: {}", translator);
  }

  public static void forEach(Function<ResponseTranslator, Boolean> function) {
    for (ResponseTranslator translator : REGISTRY) {
      if (function.apply(translator)) {
        return;
      }
    }
  }
}
