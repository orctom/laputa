package com.orctom.laputa.service.translator.content;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class TemplateContentTranslator<T> implements ContentTranslator {

  protected static final String TEMPLATE_PREFIX = "template";

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

  protected Map<String, Object> getModel(ResponseWrapper responseWrapper) {
    Map<String, Object> data = responseWrapper.getMessenger().getData();
    boolean isDataEmpty = null == data || data.isEmpty();
    boolean isResultEmpty = null == responseWrapper.getResult();

    if (isDataEmpty && isResultEmpty) {
      return Collections.emptyMap();
    }

    if (isDataEmpty) { // result is not empty
      return ImmutableMap.of("model", responseWrapper.getResult());

    } else if (isResultEmpty) { // data is not empty
      return data;

    } else { // both are not empty
      return ImmutableMap.<String, Object>builder()
          .put("model", responseWrapper.getResult())
          .putAll(data)
          .build();
    }
  }

  protected T getTemplate(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) throws ExecutionException {
    String template = responseWrapper.getTemplate();
    if (Strings.isNullOrEmpty(template)) {
      throw new NullPointerException("Template path not specified, url: " + requestWrapper.getPath());
    }

    if (isDebugEnabled) {
      return getTemplate0(template);
    }

    return templates.get(template);
  }

  protected abstract T getTemplate0(String template);
}
