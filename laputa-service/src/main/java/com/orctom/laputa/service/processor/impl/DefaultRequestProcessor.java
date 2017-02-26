package com.orctom.laputa.service.processor.impl;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.orctom.laputa.service.LaputaService;
import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.config.MappingConfig;
import com.orctom.laputa.service.exception.FileUploadException;
import com.orctom.laputa.service.exception.ParameterValidationException;
import com.orctom.laputa.service.exception.RequestProcessingException;
import com.orctom.laputa.service.model.*;
import com.orctom.laputa.service.processor.PostProcessor;
import com.orctom.laputa.service.processor.PreProcessor;
import com.orctom.laputa.service.processor.RequestProcessor;
import com.orctom.laputa.service.translator.ResponseTranslator;
import com.orctom.laputa.service.translator.ResponseTranslators;
import com.orctom.laputa.service.translator.TemplateResponseTranslator;
import com.orctom.laputa.service.util.ArgsResolver;
import com.orctom.laputa.service.util.ParamResolver;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastMethod;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * request processor
 * Created by hao on 1/6/16.
 */
public class DefaultRequestProcessor implements RequestProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestProcessor.class);

  private static final Charset UTF8 = Charset.forName("UTF-8");

  private static final byte[] ERROR_CONTENT = "500".getBytes();
  private static final byte[] ERROR_BUSY = "500, too busy".getBytes();

  private static final String FILE = ".file";
  private static final String FILENAME = ".originalFilename";
  private static final String CONTENT_TYPE = ".contentType";

  private static final Map<HttpMethod, HTTPMethod> HTTP_METHODS = ImmutableMap.of(
      HttpMethod.DELETE, HTTPMethod.DELETE,
      HttpMethod.HEAD, HTTPMethod.HEAD,
      HttpMethod.OPTIONS, HTTPMethod.OPTIONS,
      HttpMethod.POST, HTTPMethod.POST,
      HttpMethod.PUT, HTTPMethod.PUT
  );

  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  private static RateLimiter rateLimiter;

  public DefaultRequestProcessor() {
    Integer maxRequestsPerSecond = Configurator.getInstance().getRequestRateLimit();
    if (null == maxRequestsPerSecond) {
      return;
    }

    rateLimiter = RateLimiter.create(maxRequestsPerSecond);
  }

  @Override
  public ResponseWrapper handleRequest(FullHttpRequest request) {
    RequestWrapper requestWrapper = getRequestWrapper(request);

    String accept = request.headers().get(HttpHeaderNames.ACCEPT);
    ResponseTranslator translator = ResponseTranslators.getTranslator(requestWrapper, accept);

    if (null != rateLimiter && !rateLimiter.tryAcquire(200, TimeUnit.MILLISECONDS)) {
      return new ResponseWrapper(translator.getMediaType(), ERROR_BUSY);
    }

    return handleRequest(request.headers(), requestWrapper, translator);
  }

  private ResponseWrapper handleRequest(HttpHeaders headers, RequestWrapper requestWrapper, ResponseTranslator translator) {
    Context ctx = getContext(requestWrapper.getPath(), headers);

    // pre-processors
    preProcess(requestWrapper, ctx);

    try {
      RequestMapping mapping = MappingConfig.getInstance().getMapping(
          requestWrapper.getPath(),
          getHttpMethod(requestWrapper.getHttpMethod())
      );

      String permanentRedirectTo = mapping.getRedirectTo();
      if (!Strings.isNullOrEmpty(permanentRedirectTo)) {
        return new ResponseWrapper(permanentRedirectTo, true);
      }

      Object data;
      try {
        data = processRequest(requestWrapper, ctx, mapping);
      } catch (ParameterValidationException e) {
        data = new ValidationError(e.getMessages());
        ctx.put("error", e.getMessage());
        onValidationError(translator, requestWrapper, ctx);
      }

      String redirectTo = ctx.getRedirectTo();
      if (!Strings.isNullOrEmpty(redirectTo)) {
        if (null != data) {
          LOGGER.warn("`return null;` probably is missing after `context.redirectTo(...`");
        }
        return new ResponseWrapper(redirectTo, false);
      }

      // post-processors
      Object processed = postProcess(data);

      byte[] content = translator.translate(mapping, processed, ctx);
      return new ResponseWrapper(translator.getMediaType(), content);
    } catch (ParameterValidationException e) {
      return new ResponseWrapper(translator.getMediaType(), e.getMessage().getBytes(UTF8));
    } catch (Throwable e) {
      LOGGER.error(e.getMessage(), e);
      return new ResponseWrapper(translator.getMediaType(), ERROR_CONTENT);
    }
  }

  private RequestWrapper getRequestWrapper(FullHttpRequest request) {
    HttpMethod method = request.method();
    String uri = request.uri();
    LOGGER.debug("uri = {}", uri);

    if (HttpMethod.POST.equals(method) ||
        HttpMethod.PUT.equals(method) ||
        HttpMethod.PATCH.equals(method)) {
      return wrapPostRequest(request);
    } else {
      return wrapGetRequest(request, method, uri);
    }
  }

  private Context getContext(String uri, HttpHeaders headers) {
    Context ctx = new Context();
    ctx.put("uri", uri);
    ctx.put("referer", headers.get(HttpHeaderNames.REFERER));
    return ctx;
  }

  private RequestWrapper wrapPostRequest(FullHttpRequest request) {
    HttpPostRequestDecoder decoder = getHttpPostRequestDecoder(request);
    List<InterfaceHttpData> bodyDatas = decoder.getBodyHttpDatas();

    Map<String, List<String>> parameters = new HashMap<>();

    for (InterfaceHttpData bodyData : bodyDatas) {
      if (HttpDataType.Attribute == bodyData.getHttpDataType()) {
        Attribute attribute = (Attribute) bodyData;
        addToParameters(parameters, attribute);
      } else if (HttpDataType.FileUpload == bodyData.getHttpDataType()) {
        FileUpload fileUpload = (FileUpload) bodyData;
        addToParameters(parameters, fileUpload);
        decoder.removeHttpDataFromClean(bodyData);
      }
    }

    String data = getRequestData(request);
    return new RequestWrapper(request.method(), request.uri(), parameters, data);
  }

  private void addToParameters(Map<String, List<String>> parameters, Attribute attribute) {
    try {
      String value = attribute.getValue();
      if (Strings.isNullOrEmpty(value)) {
        return;
      }

      String name = attribute.getName();
      List<String> params = parameters.computeIfAbsent(name, k -> new ArrayList<>());
      params.add(value);
    } catch (IOException e) {
      throw new RequestProcessingException(e.getMessage(), e);
    }
  }

  private void addToParameters(Map<String, List<String>> parameters, FileUpload fileUpload) {
    try {
      File uploadedFile = fileUpload.getFile();
      parameters.put(fileUpload.getName() + FILE, Lists.newArrayList(uploadedFile.getAbsolutePath()));
      parameters.put(fileUpload.getName() + FILENAME, Lists.newArrayList(fileUpload.getFilename()));
      parameters.put(fileUpload.getName() + CONTENT_TYPE, Lists.newArrayList(fileUpload.getContentType()));
    } catch (IOException e) {
      throw new FileUploadException("Failed to upload file: " + e.getMessage(), e);
    }
  }

  private RequestWrapper wrapGetRequest(FullHttpRequest request, HttpMethod method, String uri) {
    QueryStringDecoder queryStringDecoder = getQueryStringDecoder(uri);
    String path = queryStringDecoder.path();
    Map<String, List<String>> queryParameters = queryStringDecoder.parameters();
    String data = getRequestData(request);
    return new RequestWrapper(method, path, queryParameters, data);
  }

  private String getRequestData(FullHttpRequest request) {
    return request.content().toString(CharsetUtil.UTF_8);
  }

  private void onValidationError(ResponseTranslator translator, RequestWrapper requestWrapper, Context ctx) {
    if (translator instanceof TemplateResponseTranslator) {
      String referer = (String) ctx.get("referer");
      if (Strings.isNullOrEmpty(referer)) {
        ctx.redirectTo("/403");
        return;
      }
      StringBuilder url = new StringBuilder(referer);
      if (referer.contains("?")) {
        url.append("&");
      } else {
        url.append("&");
      }
      url.append("error=").append(ctx.get("error"));
      ctx.redirectTo(url.toString());
    }
  }

  private <T> Collection<T> getBeansOfType(Class<T> type) {
    return LaputaService.getInstance().getApplicationContext().getBeansOfType(type).values();
  }

  private void preProcess(RequestWrapper requestWrapper, Context ctx) {
    Collection<PreProcessor> preProcessors = getBeansOfType(PreProcessor.class);
    if (preProcessors.isEmpty()) {
      return;
    }

    for (PreProcessor processor : preProcessors) {
      processor.process(requestWrapper, ctx);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("pre-processed, {}", requestWrapper.toString());
    }
  }

  private Object postProcess(Object data) {
    Collection<PostProcessor> postProcessors = getBeansOfType(PostProcessor.class);
    if (postProcessors.isEmpty()) {
      return data;
    }

    List<PostProcessor> processors = new ArrayList<>(postProcessors);
    Object processed = data;
    if (processors.size() > 1) {
      processors.sort(Comparator.comparingInt(PostProcessor::getOrder));
    }
    for (PostProcessor processor : processors) {
      LOGGER.debug("processing post-processor: #{}", processor.getOrder());
      processed = processor.process(processed);
    }

    return processed;
  }

  private QueryStringDecoder getQueryStringDecoder(String uri) {
    Charset charset = Configurator.getInstance().getCharset();
    if (null != charset) {
      return new QueryStringDecoder(uri, charset);
    } else {
      return new QueryStringDecoder(uri);
    }
  }

  private HttpPostRequestDecoder getHttpPostRequestDecoder(HttpRequest request) {
    Charset charset = Configurator.getInstance().getCharset();
    if (null != charset) {
      return new HttpPostRequestDecoder(new DefaultHttpDataFactory(true, charset), request);
    } else {
      return new HttpPostRequestDecoder(new DefaultHttpDataFactory(true), request);
    }
  }

  private HTTPMethod getHttpMethod(HttpMethod method) {
    HTTPMethod httpMethod = HTTP_METHODS.get(method);
    if (null != httpMethod) {
      return httpMethod;
    }

    return HTTPMethod.GET;
  }

  private Object processRequest(RequestWrapper requestWrapper, Context ctx, RequestMapping mapping)
      throws InvocationTargetException, IllegalAccessException {
    FastMethod handlerMethod = mapping.getHandlerMethod();
    Object target = mapping.getTarget();

    Parameter[] methodParameters = mapping.getParameters();
    if (0 == methodParameters.length) {
      return handlerMethod.invoke(target, null);
    }

    // process @Data

    Map<String, String> params = ParamResolver.extractParams(
        handlerMethod.getJavaMethod(),
        mapping.getUriPattern(),
        requestWrapper
    );

    Object[] args = ArgsResolver.resolveArgs(methodParameters, params, ctx);

    validate(target, handlerMethod.getJavaMethod(), args);

    try {
      return handlerMethod.invoke(target, args);
    } catch (InvocationTargetException e) {
      LOGGER.error("Some parameter is missing while invoking " + handlerMethod.getJavaMethod());
      throw new ParameterValidationException("Some parameter is missing, please check the API doc.");
    }
  }

  private void validate(Object target, Method method, Object[] args) {
    ExecutableValidator executableValidator = VALIDATOR.forExecutables();
    Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(target, method, args);
    if (violations.isEmpty()) {
      return;
    }

    throw new ParameterValidationException(violations);
  }
}
