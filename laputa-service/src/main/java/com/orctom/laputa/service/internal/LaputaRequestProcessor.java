package com.orctom.laputa.service.internal;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.exception.FileUploadException;
import com.orctom.laputa.service.exception.RequestProcessingException;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.processor.RequestProcessor;
import com.orctom.laputa.service.translator.content.ContentTranslator;
import com.orctom.laputa.service.translator.content.ContentTranslators;
import com.orctom.laputa.service.translator.content.TemplateContentTranslator;
import com.orctom.laputa.service.translator.response.ResponseTranslators;
import com.orctom.laputa.utils.SimpleMeter;
import com.orctom.laputa.utils.SimpleMetrics;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

import static com.orctom.laputa.service.Constants.PATH_500;
import static com.orctom.laputa.service.model.MediaType.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

/**
 * request processor
 * Created by hao on 1/6/16.
 */
class LaputaRequestProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaputaRequestProcessor.class);

  private static SimpleMeter simpleMeter;
  private static final String METER_REQUESTS = "requests";

  private static final String FILE = ".file";
  private static final String FILENAME = ".originalFilename";

  private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(
      Configurator.getInstance().getPostDataUseDiskThreshold(),
      Configurator.getInstance().getCharset()
  );

  private static final MimetypesFileTypeMap MIMETYPES_FILE_TYPE_MAP = new MimetypesFileTypeMap();

  private List<RequestProcessor> requestProcessors = new ArrayList<>();

  private static RateLimiter rateLimiter;

  LaputaRequestProcessor() {
    if (LOGGER.isInfoEnabled()) {
      simpleMeter = SimpleMetrics.create(LOGGER).meter(METER_REQUESTS);
    }

    initRateLimiter();

    loadRequestProcessors();
  }

  private void initRateLimiter() {
    Integer maxRequestsPerSecond = Configurator.getInstance().getThrottle();
    if (null != maxRequestsPerSecond) {
      rateLimiter = RateLimiter.create(maxRequestsPerSecond);
    }
  }

  private void loadRequestProcessors() {
    ServiceLoader.load(RequestProcessor.class).forEach(requestProcessors::add);
    requestProcessors.sort(Comparator.comparingInt(RequestProcessor::getOrder));
    requestProcessors.add(new DefaultRequestProcessor());
  }

  void handleRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
    if (LOGGER.isInfoEnabled()) {
      simpleMeter.mark();
    }

    RequestWrapper requestWrapper = getRequestWrapper(req);

    String mediaType = MIMETYPES_FILE_TYPE_MAP.getContentType(requestWrapper.getPath());
    ResponseWrapper responseWrapper = new ResponseWrapper(mediaType);

    try {
      if (null != rateLimiter && !rateLimiter.tryAcquire(200, TimeUnit.MILLISECONDS)) {
        responseWrapper.setStatus(TOO_MANY_REQUESTS);
      }

      long start = System.currentTimeMillis();

      processRequest(requestWrapper, responseWrapper);
      translateContent(requestWrapper, responseWrapper);

      long end = System.currentTimeMillis();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("{} took: {}ms", requestWrapper.getPath(), (end - start));
      }

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      responseWrapper.setMediaType(TEXT_PLAIN.getValue());
      responseWrapper.setStatus(BAD_REQUEST);
      responseWrapper.setContent(e.getMessage().getBytes());

    } finally {
      sendResponse(ctx, req, responseWrapper);
    }
  }

  private void processRequest(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    for (RequestProcessor requestProcessor : requestProcessors) {
      requestProcessor.handleRequest(requestWrapper, responseWrapper);
      if (responseWrapper.hasContent()) {
        return;
      }
    }

    LOGGER.error("Unhandled request: {}", requestWrapper.getUri());
  }

  private void translateContent(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    if (null != responseWrapper.getMessenger().getRedirectTo()) {
      return;
    }

    ContentTranslator translator = ContentTranslators.getTranslator(requestWrapper);
    if (null == responseWrapper.getResult() && !(translator instanceof TemplateContentTranslator)) {
      return;
    }

    try {
      byte[] content = translator.translate(responseWrapper);
      responseWrapper.setContent(content);

    } catch (IOException e) {
      responseWrapper.setRedirectTo(PATH_500);
      responseWrapper.setData("error", INTERNAL_SERVER_ERROR.reasonPhrase());
      LOGGER.error(e.getMessage(), e);
    }
  }

  private void sendResponse(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    ResponseTranslators.search(translator -> {
      if (translator.fits(responseWrapper)) {
        translator.translate(ctx, req, responseWrapper);
        return true;

      } else {
        return false;
      }
    });
  }

  private RequestWrapper getRequestWrapper(FullHttpRequest request) {
    HttpMethod method = request.method();
    String uri = request.uri();

    if (HttpMethod.POST.equals(method) ||
        HttpMethod.PUT.equals(method) ||
        HttpMethod.PATCH.equals(method)) {
      return wrapPostRequest(request);
    } else {
      return wrapGetRequest(request, method, uri);
    }
  }

  private RequestWrapper wrapPostRequest(FullHttpRequest request) {
    String data = getRequestData(request);

    HttpPostRequestDecoder decoder;
    try {
      decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, request);
    } catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
      LOGGER.error("Decoder exception: {}", data);
      throw new RequestProcessingException(e.getMessage(), e);
    }

    List<InterfaceHttpData> bodyDatas = decoder.getBodyHttpDatas();

    Map<String, List<String>> parameters = new HashMap<>();

    String uri = request.uri();

    try {
      for (InterfaceHttpData bodyData : bodyDatas) {
        if (HttpDataType.Attribute == bodyData.getHttpDataType()) {
          Attribute attribute = (Attribute) bodyData;
          addToParameters(parameters, attribute);

        } else if (HttpDataType.FileUpload == bodyData.getHttpDataType()) {
          FileUpload fileUpload = (FileUpload) bodyData;
          addToParameters(parameters, fileUpload);
        }
      }

      return new RequestWrapper(request.method(), request.headers(), uri, uri, parameters, data);

    } catch (HttpPostRequestDecoder.EndOfDataDecoderException e) {
      return new RequestWrapper(request.method(), request.headers(), uri, uri, parameters, data);

    } finally {
      decoder.destroy();
    }
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
    return new RequestWrapper(method, request.headers(), uri, path, queryParameters, data);
  }

  private String getRequestData(FullHttpRequest request) {
    return request.content().toString(CharsetUtil.UTF_8);
  }

  private QueryStringDecoder getQueryStringDecoder(String uri) {
    Charset charset = Configurator.getInstance().getCharset();
    if (null != charset) {
      return new QueryStringDecoder(uri, charset);
    } else {
      return new QueryStringDecoder(uri);
    }
  }
}
