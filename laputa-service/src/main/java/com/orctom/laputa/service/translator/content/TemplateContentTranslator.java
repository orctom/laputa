package com.orctom.laputa.service.translator.content;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.model.MediaType;

import java.util.concurrent.ExecutionException;

public abstract class TemplateContentTranslator<T> implements ContentTranslator {

  protected static final String TEMPLATE_PREFIX = "/template";

  protected static final MediaType TYPE = MediaType.TEXT_HTML;

  protected static final boolean isDebugEnabled = Configurator.getInstance().isDebugEnabled();

  private LoadingCache<String, T> templates = CacheBuilder.newBuilder()
      .build(
          new CacheLoader<String, T>() {
            @Override
            public T load(String template) throws Exception {
              return getTemplate0(template);
            }
          }
      );

  @Override
  public final String getMediaType() {
    return TYPE.getValue();
  }

  @Override
  public final String getExtension() {
    return TYPE.getExtension();
  }

  protected T getTemplate(String template) throws ExecutionException {
    if (isDebugEnabled) {
      return getTemplate0(template);
    }

    return templates.get(template);
  }

  protected abstract T getTemplate0(String template);
}
