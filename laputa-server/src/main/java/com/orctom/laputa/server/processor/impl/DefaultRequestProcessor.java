package com.orctom.laputa.server.processor.impl;

import com.google.common.collect.ImmutableMap;
import com.orctom.laputa.server.PreProcessor;
import com.orctom.laputa.server.config.MappingConfig;
import com.orctom.laputa.server.config.ServiceConfig;
import com.orctom.laputa.server.internal.BeanFactory;
import com.orctom.laputa.server.model.HTTPMethod;
import com.orctom.laputa.server.model.RequestMapping;
import com.orctom.laputa.server.model.Response;
import com.orctom.laputa.server.processor.RequestProcessor;
import com.orctom.laputa.server.translator.ResponseTranslator;
import com.orctom.laputa.server.translator.ResponseTranslators;
import com.orctom.laputa.server.util.ArgsResolver;
import com.orctom.laputa.server.util.ParamResolver;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;

/**
 * request processor
 * Created by hao on 1/6/16.
 */
public class DefaultRequestProcessor implements RequestProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestProcessor.class);

  private static final BeanFactory beanFactory = ServiceConfig.getInstance().getBeanFactory();

  private static final byte[] ERROR_CONTENT = {'5', '0', '0'};

  private static final Map<HttpMethod, HTTPMethod> HTTP_METHODS = ImmutableMap.of(
      HttpMethod.DELETE, HTTPMethod.DELETE,
      HttpMethod.HEAD, HTTPMethod.HEAD,
      HttpMethod.OPTIONS, HTTPMethod.OPTIONS,
      HttpMethod.POST, HTTPMethod.POST,
      HttpMethod.PUT, HTTPMethod.PUT
  );

  @Override
  public Response handleRequest(DefaultHttpRequest req) {
    HttpMethod method = req.method();
    String url = req.uri();
    LOGGER.debug("url      = {}", url);

    // pro-processor
    preProcess(req);

    // remove hash
    String uri = removeHashFromUri(url);

    // get query string
    int questionMarkIndex = uri.indexOf("?");
    String queryStr = null;
    if (questionMarkIndex > 0) {
      queryStr = uri.substring(questionMarkIndex + 1);
      uri = uri.substring(0, questionMarkIndex);
    }
    LOGGER.trace("uri      = {}", uri);
    LOGGER.trace("queryStr = {}", queryStr);

    RequestMapping mapping = MappingConfig.getInstance().getMapping(uri, getHttpMethod(method));

    String accept = req.headers().get(HttpHeaderNames.ACCEPT);
    ResponseTranslator translator = ResponseTranslators.getTranslator(uri, accept);
    try {
      Object data = processRequest(uri, queryStr, mapping);
      byte[] content = translator.translate(data);
      return new Response(translator.getMediaType(), content);
    } catch (Throwable e) {
      LOGGER.error(e.getMessage(), e);
      return new Response(translator.getMediaType(), ERROR_CONTENT);
    }
  }

  private void preProcess(DefaultHttpRequest req) {
    Collection<PreProcessor> preProcessors = beanFactory.getInstances(PreProcessor.class);
    if (null == preProcessors) {
      return;
    }
    for (PreProcessor processor : preProcessors) {
      processor.process(req);
    }
  }

  private String removeHashFromUri(String uri) {
    int hashIndex = uri.indexOf("#");
    if (hashIndex > 0) {
      return uri.substring(0, hashIndex);
    } else {
      return uri;
    }
  }

  private HTTPMethod getHttpMethod(HttpMethod method) {
    HTTPMethod httpMethod = HTTP_METHODS.get(method);
    if (null != httpMethod) {
      return httpMethod;
    }

    return HTTPMethod.GET;
  }

  public Object processRequest(String uri, String queryString, RequestMapping mapping)
      throws InvocationTargetException, IllegalAccessException {
    Class<?> handlerClass = mapping.getHandlerClass();
    FastMethod handlerMethod = mapping.getHandlerMethod();
    Object target = beanFactory.getInstance(handlerClass);

    Parameter[] methodParameters = mapping.getParameters();
    if (0 == methodParameters.length) {
      return handlerMethod.invoke(target, null);
    }

    Map<String, String> params = ParamResolver.extractParams(
        handlerMethod.getJavaMethod(),
        mapping.getUriPattern(),
        uri,
        queryString
    );

    Object[] args = ArgsResolver.resolveArgs(methodParameters, params);
    return handlerMethod.invoke(target, args);
  }
}
