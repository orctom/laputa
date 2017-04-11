package com.orctom.laputa.service.model;

import com.orctom.laputa.service.annotation.Data;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.orctom.laputa.service.util.ParamResolver.getDefaultValue;
import static com.orctom.laputa.service.util.ParamResolver.getParamName;

public class RequestMapping {

  private String uriPattern;
  private Object target;
  private FastMethod handlerMethod;
  private Map<String, ParamInfo> handlerParameters = Collections.emptyMap();
  private Class<?> dataType;
  private String httpMethod;
  private String redirectTo;

  public RequestMapping(String uriPattern,
                        Object target,
                        Class<?> handlerClass,
                        Method handlerMethod,
                        String httpMethod,
                        String redirectTo) {
    this.uriPattern = uriPattern;
    this.target = target;
    this.handlerMethod = FastClass.create(handlerClass).getMethod(handlerMethod);
    this.httpMethod = httpMethod;
    this.redirectTo = redirectTo;
    init(handlerMethod);
  }

  private void init(Method handlerMethod) {
    Parameter[] parameters = handlerMethod.getParameters();
    int paramLength = parameters.length;
    if (0 == paramLength) {
      return;
    }

    Map<String, ParamInfo> _handlerParameters = new LinkedHashMap<>(2 * paramLength);

    for (Parameter parameter : parameters) {
      Class<?> paramType = parameter.getType();

      if (1 == paramLength && parameter.isAnnotationPresent(Data.class)) {
        dataType = paramType;
        break;
      }

      if (Context.class.isAssignableFrom(paramType)) {
        _handlerParameters.put("_ctx_", new ParamInfo(paramType));
        continue;
      }

      String paramName = getParamName(parameter, handlerMethod);

      Annotation[] annotations = parameter.getAnnotations();
      _handlerParameters.put(paramName, new ParamInfo(getDefaultValue(parameter), paramType, annotations));
    }

    handlerParameters = Collections.unmodifiableMap(_handlerParameters);
  }

  public String getUriPattern() {
    return uriPattern;
  }

  public Object getTarget() {
    return target;
  }

  public FastMethod getHandlerMethod() {
    return handlerMethod;
  }

  public Map<String, ParamInfo> getHandlerParameters() {
    return handlerParameters;
  }

  public Class<?> getDataType() {
    return dataType;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public String getRedirectTo() {
    return redirectTo;
  }

  @Override
  public String toString() {
    return uriPattern + " " + httpMethod + " -> " + handlerMethod.getJavaMethod().toGenericString();
  }
}
