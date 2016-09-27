package com.orctom.laputa.server.processor.impl;

import com.google.common.collect.ImmutableMap;
import com.orctom.laputa.server.processor.PreProcessor;
import com.orctom.laputa.server.config.MappingConfig;
import com.orctom.laputa.server.config.ServiceConfig;
import com.orctom.laputa.server.internal.BeanFactory;
import com.orctom.laputa.server.model.HTTPMethod;
import com.orctom.laputa.server.model.RequestMapping;
import com.orctom.laputa.server.model.RequestWrapper;
import com.orctom.laputa.server.model.Response;
import com.orctom.laputa.server.processor.RequestProcessor;
import com.orctom.laputa.server.translator.ResponseTranslator;
import com.orctom.laputa.server.translator.ResponseTranslators;
import com.orctom.laputa.server.util.ArgsResolver;
import com.orctom.laputa.server.util.ParamResolver;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
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
    RequestWrapper requestWrapper = getRequestWrapper(req);

    // pro-processor
    preProcess(requestWrapper);

    RequestMapping mapping = MappingConfig.getInstance().getMapping(
        requestWrapper.getPath(),
        getHttpMethod(requestWrapper.getHttpMethod()));

    String accept = req.headers().get(HttpHeaderNames.ACCEPT);
    ResponseTranslator translator = ResponseTranslators.getTranslator(requestWrapper.getPath(), accept);
    try {
      Object data = processRequest(requestWrapper, mapping);
      byte[] content = translator.translate(data);
      return new Response(translator.getMediaType(), content);
    } catch (Throwable e) {
      LOGGER.error(e.getMessage(), e);
      return new Response(translator.getMediaType(), ERROR_CONTENT);
    }
  }

  private RequestWrapper getRequestWrapper(DefaultHttpRequest req) {
    HttpMethod method = req.method();
    String uri = req.uri();
    LOGGER.debug("uri = {}", uri);

    QueryStringDecoder queryStringDecoder = getQueryStringDecoder(uri);
    String path = queryStringDecoder.path();
    Map<String, List<String>> queryParameters = queryStringDecoder.parameters();
    return new RequestWrapper(method, path, queryParameters);
  }

  private void preProcess(RequestWrapper requestWrapper) {
    Collection<PreProcessor> preProcessors = beanFactory.getInstances(PreProcessor.class);
    if (null == preProcessors || preProcessors.isEmpty()) {
      return;
    }

    for (PreProcessor processor : preProcessors) {
      processor.process(requestWrapper);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("pre-processed, {}", requestWrapper.toString());
    }
  }

  private QueryStringDecoder getQueryStringDecoder(String uri) {
    Charset charset = ServiceConfig.getInstance().getCharset();
    if (null != charset) {
      return new QueryStringDecoder(uri, charset);
    } else {
      return new QueryStringDecoder(uri);
    }
  }

  private HTTPMethod getHttpMethod(HttpMethod method) {
    HTTPMethod httpMethod = HTTP_METHODS.get(method);
    if (null != httpMethod) {
      return httpMethod;
    }

    return HTTPMethod.GET;
  }

  public Object processRequest(RequestWrapper requestWrapper, RequestMapping mapping)
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
        requestWrapper
    );

    Object[] args = ArgsResolver.resolveArgs(methodParameters, params);
    return handlerMethod.invoke(target, args);
  }
}
