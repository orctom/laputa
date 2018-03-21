package com.orctom.laputa.service.internal;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.orctom.laputa.service.LaputaService;
import com.orctom.laputa.service.annotation.Template;
import com.orctom.laputa.service.config.MappingConfig;
import com.orctom.laputa.service.exception.ParameterValidationException;
import com.orctom.laputa.service.exception.RequestProcessingException;
import com.orctom.laputa.service.filter.Filter;
import com.orctom.laputa.service.filter.FilterChain;
import com.orctom.laputa.service.model.HTTPMethod;
import com.orctom.laputa.service.model.ParamInfo;
import com.orctom.laputa.service.model.RequestMapping;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.Response;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.model.ValidationError;
import com.orctom.laputa.service.processor.RequestProcessor;
import com.orctom.laputa.service.util.ArgsResolver;
import com.orctom.laputa.service.util.ParamResolver;
import com.orctom.laputa.utils.ClassUtils;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastMethod;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static com.orctom.laputa.service.Constants.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

public class DefaultRequestProcessor implements RequestProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestProcessor.class);

  private static final Map<HttpMethod, HTTPMethod> HTTP_METHODS = ImmutableMap.of(
      HttpMethod.DELETE, HTTPMethod.DELETE,
      HttpMethod.HEAD, HTTPMethod.HEAD,
      HttpMethod.OPTIONS, HTTPMethod.OPTIONS,
      HttpMethod.POST, HTTPMethod.POST,
      HttpMethod.PUT, HTTPMethod.PUT
  );

  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  private static final Pattern BRACE_LEFT = Pattern.compile("\\{");
  private static final Pattern BRACE_RIGHT = Pattern.compile("}");
  private static final String EMPTY_STR = "";

  private static List<Filter> filters;

  private static LoadingCache<RequestMapping, String> templates = CacheBuilder.newBuilder()
      .build(
          new CacheLoader<RequestMapping, String>() {
            @Override
            public String load(RequestMapping mapping) throws Exception {
              return getTemplate0(mapping);
            }
          }
      );

  DefaultRequestProcessor() {
    initFilterChain();
  }

  private void initFilterChain() {
    filters = new ArrayList<>(getBeansOfType(Filter.class));
    if (!filters.isEmpty()) {
      filters.sort(Comparator.comparingInt(Filter::getOrder));
    }
  }

  @Override
  public int getOrder() {
    return 1000;
  }

  @Override
  public void handleRequest(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    try {
      createFilterChain().doFilter(requestWrapper, responseWrapper);
    } catch (RequestProcessingException e) {
      throw e;
    } catch (Exception e) {
      throw new RequestProcessingException(e.getMessage(), e);
//      responseWrapper.setRedirectTo(PATH_500);
//      responseWrapper.setData("error", INTERNAL_SERVER_ERROR.reasonPhrase());
//      LOGGER.error(e.getMessage(), e);
    }
  }

  private FilterChain createFilterChain() {
    if (null == filters || filters.isEmpty()) {
      return new LaputaFilterChain(this);
    }

    return new LaputaFilterChain(this, filters);
  }

  void service(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    RequestMapping mapping = getRequestMapping(requestWrapper, responseWrapper);

    Object result;
    try {
      result = processRequest(requestWrapper, responseWrapper, mapping);

    } catch (ParameterValidationException e) {
      result = new ValidationError(e.getMessages());
      responseWrapper.setRedirectTo(PATH_403);
      responseWrapper.setData("error", e.getMessage());

    } catch (IllegalArgumentException e) {
      result = new Response(BAD_REQUEST.code(), Lists.newArrayList(BAD_REQUEST.reasonPhrase()));
      responseWrapper.setRedirectTo(PATH_403);
      responseWrapper.setData("error", BAD_REQUEST.reasonPhrase());
      LOGGER.error(e.getMessage(), e);

    } catch (Exception e) {
      throw new RequestProcessingException(e.getMessage(), e);
//      result = new Response(INTERNAL_SERVER_ERROR.code(), Lists.newArrayList(INTERNAL_SERVER_ERROR.reasonPhrase()));
//      responseWrapper.setRedirectTo(PATH_500);
//      responseWrapper.setData("error", INTERNAL_SERVER_ERROR.reasonPhrase());
//      LOGGER.error(e.getMessage(), e);
    }

    responseWrapper.setResult(result);
  }

  private RequestMapping getRequestMapping(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    MappingConfig mappingConfig = MappingConfig.getInstance();
    RequestMapping mapping = mappingConfig.getMapping(
        requestWrapper.getPath(),
        getHttpMethod(requestWrapper.getHttpMethod())
    );

    if (null == mapping) {
      mapping = mappingConfig._404();
      responseWrapper.setStatus(NOT_FOUND);
    }

    setTemplateName(responseWrapper, mapping);
    return mapping;
  }

  private void setTemplateName(ResponseWrapper responseWrapper, RequestMapping mapping) {
    String template;
    try {
      template = templates.get(mapping);
    } catch (ExecutionException e) {
      LOGGER.error(e.getMessage(), e);
      template = getTemplate0(mapping);
    }

    responseWrapper.setTemplate(template);
  }

  private static String getTemplate0(RequestMapping mapping) {
    Template template = mapping.getHandlerMethod().getJavaMethod().getAnnotation(Template.class);
    if (null != template) {
      return transformIndex(template.value());
    }

    return transformIndex(normalized(mapping.getUriPattern()));
  }

  /**
   * Removing brackets
   */
  private static String normalized(String uriPattern) {
    return BRACE_RIGHT.matcher(
        BRACE_LEFT.matcher(uriPattern).replaceAll(EMPTY_STR)
    ).replaceAll(EMPTY_STR);
  }

  private static String transformIndex(String template) {
    if (Strings.isNullOrEmpty(template)) {
      return PATH_INDEX;
    }
    if (template.endsWith(PATH_SEPARATOR)) {
      return template + PATH_INDEX;
    }
    return template;
  }

  private Object processRequest(RequestWrapper requestWrapper, ResponseWrapper responseWrapper, RequestMapping mapping)
      throws InvocationTargetException {
    FastMethod handlerMethod = mapping.getHandlerMethod();
    Object target = mapping.getTarget();

    // process @Data
    Class<?> dataType = mapping.getDataType();
    if (null != dataType) {
      if (ClassUtils.isSimpleValueType(dataType)) {
        return handlerMethod.invoke(target, new Object[]{requestWrapper.getData()});

      } else {
        Object arg = JSON.parseObject(requestWrapper.getData(), dataType);
        return handlerMethod.invoke(target, new Object[]{arg});
      }
    }

    Map<String, ParamInfo> parameters = mapping.getHandlerParameters();
    if (parameters.isEmpty()) {
      return handlerMethod.invoke(target, null);
    }

    Map<String, String> params = ParamResolver.extractParams(mapping, requestWrapper);

    Object[] args = ArgsResolver.resolveArgs(params, parameters, requestWrapper, responseWrapper.getMessenger());

    validate(target, handlerMethod.getJavaMethod(), args);

    try {
      return handlerMethod.invoke(target, args);

    } catch (InvocationTargetException e) {
      throw new RequestProcessingException(e.getTargetException().getMessage(), e);

    } catch (Exception e) {
      throw new RequestProcessingException(e.getMessage(), e);
    }
  }

  private HTTPMethod getHttpMethod(HttpMethod method) {
    HTTPMethod httpMethod = HTTP_METHODS.get(method);
    if (null != httpMethod) {
      return httpMethod;
    }

    return HTTPMethod.GET;
  }

  private void validate(Object target, Method method, Object[] args) {
    ExecutableValidator executableValidator = VALIDATOR.forExecutables();
    Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(target, method, args);
    if (violations.isEmpty()) {
      return;
    }

    throw new ParameterValidationException(violations);
  }

  private <T> Collection<T> getBeansOfType(Class<T> type) {
    return LaputaService.getInstance().getApplicationContext().getBeansOfType(type).values();
  }
}
