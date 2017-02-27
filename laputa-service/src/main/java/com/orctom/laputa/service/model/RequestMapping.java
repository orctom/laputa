package com.orctom.laputa.service.model;

import com.orctom.laputa.exception.IllegalConfigException;
import com.orctom.laputa.service.annotation.Data;
import com.orctom.laputa.service.annotation.DefaultValue;
import com.orctom.laputa.service.annotation.Param;
import com.orctom.laputa.utils.ClassUtils;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestMapping {

  private String uriPattern;
  private Object target;
  private FastMethod handlerMethod;
  private Map<String, String> paramDefaultValues = Collections.emptyMap();
  private Map<String, Class<?>> paramTypes = Collections.emptyMap();
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

    Map<String, String> _paramDefaultValues = new LinkedHashMap<>();
    Map<String, Class<?>> _paramTypes = new LinkedHashMap<>();
    for (Parameter parameter : parameters) {
      Class<?> paramType = parameter.getType();
      if (1 == paramLength && parameter.isAnnotationPresent(Data.class)) {
        dataType = paramType;
        break;
      }

      if (Context.class.isAssignableFrom(paramType)) {
        continue;
      }

      Param param = parameter.getAnnotation(Param.class);
      if (null == param) {
        if (parameter.isAnnotationPresent(Data.class)) {
          throw new IllegalConfigException("Only one param is allowed when using @Data annotation, at " + handlerMethod.toString());
        } else {
          throw new IllegalConfigException("Missing @Param annotation at " + handlerMethod.toString());
        }
      }
      String paramName = param.value();

      DefaultValue defaultValue = parameter.getAnnotation(DefaultValue.class);
      if (null != defaultValue && ClassUtils.isSimpleValueType(paramType)) {
        _paramDefaultValues.put(paramName, defaultValue.value());
      }

      _paramTypes.put(paramName, paramType);
    }

    paramDefaultValues = Collections.unmodifiableMap(_paramDefaultValues);
    paramTypes = Collections.unmodifiableMap(_paramTypes);
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

  public Map<String, String> getParamDefaultValues() {
    return paramDefaultValues;
  }

  public Map<String, Class<?>> getParamTypes() {
    return paramTypes;
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
