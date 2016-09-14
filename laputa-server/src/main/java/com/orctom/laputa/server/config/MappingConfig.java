package com.orctom.laputa.server.config;

import com.google.common.base.Strings;
import com.orctom.laputa.exception.ClassLoadingException;
import com.orctom.laputa.server.annotation.*;
import com.orctom.laputa.server.internal.handler.DefaultHandler;
import com.orctom.laputa.server.model.HTTPMethod;
import com.orctom.laputa.server.model.PathTrie;
import com.orctom.laputa.server.model.RequestMapping;
import com.orctom.laputa.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Holding url mappings...
 * Created by hao on 9/21/15.
 */
public class MappingConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(MappingConfig.class);

  private Map<String, RequestMapping> staticMappings = new HashMap<>();
  private PathTrie wildcardMappings = new PathTrie();

  private static final Pattern PATTERN_DOUBLE_SLASHES = Pattern.compile("//");
  private static final Pattern PATTERN_TAIL_SLASH = Pattern.compile("/$");
  private static final String KEY_PATH_PARAM = "{*}";

  private static final MappingConfig INSTANCE = new MappingConfig();

  private MappingConfig() {
  }

  public static MappingConfig getInstance() {
    return INSTANCE;
  }

  public RequestMapping getMapping(String uri, HTTPMethod httpMethod) {
    String path = uri;
    int dotIndex = path.lastIndexOf(".");
    if (dotIndex > 0) {
      path = path.substring(0, dotIndex);
    }
    RequestMapping handler = staticMappings.get(path + "/" + httpMethod.getKey());
    if (null != handler) {
      return handler;
    }

    handler = getHandlerForWildcardUri(path, httpMethod);
    if (null != handler) {
      return handler;
    }

    return _404();
  }

  private RequestMapping _404() {
    return staticMappings.get("/404/@get");
  }

  /**
   * There are 4 types of `paths`:<br/>
   * <li>1) static at middle of the uri</li>
   * <li>2) static at end of the uri</li>
   * <li>3) dynamic at middle of the uri</li>
   * <li>4) dynamic at end of the uri</li>
   */
  private RequestMapping getHandlerForWildcardUri(String uri, HTTPMethod httpMethod) {
    String[] paths = uri.split("/");
    if (paths.length < 2) {
      return null;
    }

    PathTrie parent = wildcardMappings;

    for (int i = 1; i < paths.length; i++) {
      String path = paths[i];
      if (Strings.isNullOrEmpty(path)) {
        continue;
      }

      boolean isEndPath = i == paths.length - 1;

      // 1) and 2)
      PathTrie child = parent.getChildren().get(path);
      if (null != child) {
        if (isEndPath) {
          return child.getChildren().get(httpMethod.getKey()).getHandler();
        }
        parent = child;
        continue;
      }

      // 3) and 4)
      child = parent.getChildren().get(KEY_PATH_PARAM);
      if (null != child) {
        if (isEndPath) {
          return child.getChildren().get(httpMethod.getKey()).getHandler();
        }
        parent = child;
      }
    }

    return null;
  }

  public void scan(Class<? extends Annotation> annotation, String... basePackages) throws ClassLoadingException {
    List<Class<?>> services = new ArrayList<>();

    services.add(DefaultHandler.class);

    for (String packageName : basePackages) {
      services.addAll(ClassUtils.getClassesWithAnnotation(packageName, annotation));
    }

    services.forEach(this::configureMappings);

    logMappingInfo();
  }

  private void logMappingInfo() {
    LOGGER.info("static mappings:");
    for (RequestMapping handler : new TreeMap<>(staticMappings).values()) {
      LOGGER.info(handler.toString());
    }

    LOGGER.info("dynamic mappings:");
    LOGGER.info(wildcardMappings.getChildrenMappings());
  }

  protected void configureMappings(Class<?> clazz) {
    String basePath = "";
    if (clazz.isAnnotationPresent(Path.class)) {
      basePath = clazz.getAnnotation(Path.class).value();
    }

    for (Method method : clazz.getMethods()) {
      Path path = method.getAnnotation(Path.class);
      if (null != path) {
        String pathValue = path.value().trim();
        if (Strings.isNullOrEmpty(pathValue)) {
          throw new IllegalArgumentException(
              "Empty value of Path annotation on " + clazz.getCanonicalName() + " " + method.getName());
        }
        String uri = basePath + pathValue;
        addToMappings(clazz, method, uri);
      }
    }
  }

  private HTTPMethod getHttpMethod(Method method) {
    if (method.isAnnotationPresent(POST.class)) {
      return HTTPMethod.POST;
    }
    if (method.isAnnotationPresent(PUT.class)) {
      return HTTPMethod.PUT;
    }
    if (method.isAnnotationPresent(DELETE.class)) {
      return HTTPMethod.DELETE;
    }
    if (method.isAnnotationPresent(HEAD.class)) {
      return HTTPMethod.HEAD;
    }
    if (method.isAnnotationPresent(OPTIONS.class)) {
      return HTTPMethod.OPTIONS;
    }
    return HTTPMethod.GET;
  }

  private void addToMappings(Class<?> clazz, Method method, String rawPath) {
    String uri = normalize(rawPath);
    String httpMethodKey = getHttpMethod(method).getKey();
    if (uri.contains("{")) {
      addToWildCardMappings(clazz, method, uri, httpMethodKey);
    } else {
      RequestMapping mapping = staticMappings.put(uri + "/" + httpMethodKey, new RequestMapping(uri, clazz, method));
      if (null != mapping) {
        throw new IllegalArgumentException("Conflicts found in configured @Path:\n" + uri + ", " + httpMethodKey +
            "\n\t\t" + mapping.getHandlerMethod().toString() + "\n\t\t" + method.toString());
      }
    }
  }

  private String normalize(String uri) {
    uri = "/" + uri;
    uri = PATTERN_DOUBLE_SLASHES.matcher(uri).replaceAll("/");
    uri = PATTERN_TAIL_SLASH.matcher(uri).replaceAll("");
    return uri;
  }

  private void addToWildCardMappings(Class<?> clazz, Method method, String uri, String httpMethodKey) {
    String[] paths = uri.split("/");

    if (paths.length < 2) {
      return;
    }

    Map<String, PathTrie> children = wildcardMappings.getChildren();

    for (String path : paths) {
      if (Strings.isNullOrEmpty(path)) {
        continue;
      }

      boolean containsParam = path.contains("{");
      String pathKey = containsParam ? KEY_PATH_PARAM : path;
      PathTrie child = children.get(pathKey);

      if (null == child) {
        child = new PathTrie();
        children.put(pathKey, child);
      }

      children = child.getChildren();
    }

    PathTrie leaf = new PathTrie(uri, clazz, method);
    children.put(httpMethodKey, leaf);
  }

}
