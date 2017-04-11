package com.orctom.laputa.service.internal;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.orctom.laputa.service.LaputaService;
import com.orctom.laputa.service.config.MappingConfig;
import com.orctom.laputa.service.exception.ParameterValidationException;
import com.orctom.laputa.service.exception.RequestProcessingException;
import com.orctom.laputa.service.filter.Filter;
import com.orctom.laputa.service.filter.FilterChain;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.HTTPMethod;
import com.orctom.laputa.service.model.ParamInfo;
import com.orctom.laputa.service.model.RequestMapping;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.Response;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.model.ValidationError;
import com.orctom.laputa.service.processor.RequestProcessor;
import com.orctom.laputa.service.translator.content.ContentTranslator;
import com.orctom.laputa.service.translator.content.ContentTranslators;
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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.orctom.laputa.service.Constants.PATH_403;
import static com.orctom.laputa.service.Constants.PATH_500;
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

  private static FilterChain filterChain;

  public DefaultRequestProcessor() {
    initFilterChain();
  }

  private void initFilterChain() {
    List<Filter> filters = new ArrayList<>(getBeansOfType(Filter.class));
    if (filters.isEmpty()) {
      filterChain = new LaputaFilterChain(this);

    } else {
      filters.sort(Comparator.comparingInt(Filter::getOrder));
      filterChain = new LaputaFilterChain(this, filters);
    }
  }

  @Override
  public int getOrder() {
    return 1000;
  }

  @Override
  public void handleRequest(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    try {
      filterChain.doFilter(requestWrapper, responseWrapper);
    } catch (Exception e) {
      responseWrapper.setRedirectTo(PATH_500);
      responseWrapper.setData("error", INTERNAL_SERVER_ERROR.reasonPhrase());
      LOGGER.error(e.getMessage(), e);
    }
  }

  void service(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    MappingConfig mappingConfig = MappingConfig.getInstance();
    RequestMapping mapping = mappingConfig.getMapping(
        requestWrapper.getPath(),
        getHttpMethod(requestWrapper.getHttpMethod())
    );

    if (null == mapping) {
      mapping = mappingConfig._404();
      responseWrapper.setStatus(NOT_FOUND);
    }

    Context ctx = getContext(requestWrapper);
    Object data;
    try {
      data = processRequest(requestWrapper, ctx, mapping);
      responseWrapper.setCookies(ctx.getCookies());

    } catch (ParameterValidationException e) {
      data = new ValidationError(e.getMessages());
      responseWrapper.setRedirectTo(PATH_403);
      responseWrapper.setData("error", e.getMessage());

    } catch (IllegalArgumentException e) {
      data = new Response(BAD_REQUEST.code(), Lists.newArrayList(BAD_REQUEST.reasonPhrase()));
      responseWrapper.setRedirectTo(PATH_403);
      responseWrapper.setData("error", BAD_REQUEST.reasonPhrase());
      LOGGER.error(e.getMessage(), e);

    } catch (Exception e) {
      data = new Response(INTERNAL_SERVER_ERROR.code(), Lists.newArrayList(INTERNAL_SERVER_ERROR.reasonPhrase()));
      responseWrapper.setRedirectTo(PATH_500);
      responseWrapper.setData("error", INTERNAL_SERVER_ERROR.reasonPhrase());
      LOGGER.error(e.getMessage(), e);
    }

    ContentTranslator translator = ContentTranslators.getTranslator(requestWrapper);
    try {
      byte[] content = translator.translate(mapping, data, ctx);
      responseWrapper.setContent(content);
    } catch (IOException e) {
      responseWrapper.setRedirectTo(PATH_500);
      responseWrapper.setData("error", INTERNAL_SERVER_ERROR.reasonPhrase());
      LOGGER.error(e.getMessage(), e);
    }
  }

  private Object processRequest(RequestWrapper requestWrapper, Context ctx, RequestMapping mapping)
      throws InvocationTargetException, IllegalAccessException {
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

    Object[] args = ArgsResolver.resolveArgs(params, parameters, requestWrapper, ctx);

    validate(target, handlerMethod.getJavaMethod(), args);

    try {
      return handlerMethod.invoke(target, args);

    } catch (InvocationTargetException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RequestProcessingException(e.getTargetException().getMessage());

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new RequestProcessingException(e.getMessage());
    }
  }

  private HTTPMethod getHttpMethod(HttpMethod method) {
    HTTPMethod httpMethod = HTTP_METHODS.get(method);
    if (null != httpMethod) {
      return httpMethod;
    }

    return HTTPMethod.GET;
  }

  private Context getContext(RequestWrapper requestWrapper) {
    return new Context(requestWrapper.getPath());
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
