package com.orctom.laputa.service.processor;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.utils.FileUtils;
import com.typesafe.config.Config;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.orctom.laputa.service.Constants.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

public class WebRequestProcessor implements RequestProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebRequestProcessor.class);

  private static final Configurator CONFIGURATOR = Configurator.getInstance();
  private static final boolean isDebugEnabled = CONFIGURATOR.isDebugEnabled();

  private static Map<String, String> staticFileMapping = new HashMap<>();
  private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

  private final LoadingCache<String, byte[]> classpathStaticFileContentCache = CacheBuilder.newBuilder()
      .softValues()
      .maximumSize(500)
      .build(new CacheLoader<String, byte[]>() {
        @Override
        public byte[] load(String resource) throws Exception {
          return getContentAsByteArray(resource);
        }
      });

  public WebRequestProcessor() {
    initStaticPaths();
  }

  @SuppressWarnings("unchecked")
  private static void initStaticPaths() {
    Config config = CONFIGURATOR.getConfig();
    addToStaticFileMapping((List<Config>) config.getConfigList(CFG_URLS_STATIC_DEFAULT_MAPPINGS));
    if (config.hasPath(CFG_URLS_STATIC_MAPPINGS)) {
      addToStaticFileMapping((List<Config>) config.getConfigList(CFG_URLS_STATIC_MAPPINGS));
    }
  }

  private static void addToStaticFileMapping(List<Config> staticFileMappingsConfig) {
    if (null == staticFileMappingsConfig || staticFileMappingsConfig.isEmpty()) {
      return;
    }

    for (Config staticFileMappingConfig : staticFileMappingsConfig) {
      String uri = staticFileMappingConfig.getString(CFG_URI);
      String path = staticFileMappingConfig.getString(CFG_PATH);
      staticFileMapping.put(uri, path);
      LOGGER.info("Added static content mapping: {} -> {}", uri, path);
    }
  }

  @Override
  public boolean canHandleRequest(RequestWrapper requestWrapper) {
    String staticFilePath = getStaticFileMappingPath(requestWrapper.getPath());
    return !Strings.isNullOrEmpty(staticFilePath);
  }

  @Override
  public ResponseWrapper handleRequest(RequestWrapper requestWrapper, String mediaType) {
    String staticFilePath = getStaticFileMappingPath(requestWrapper.getPath());
    return handleStaticFileRequest(requestWrapper, mediaType, staticFilePath);
  }

  private String getStaticFileMappingPath(String uri) {
    for (Map.Entry<String, String> entry : staticFileMapping.entrySet()) {
      if (uri.startsWith(entry.getKey())) {
        return entry.getValue();
      }
    }
    return null;
  }

  private ResponseWrapper handleStaticFileRequest(RequestWrapper requestWrapper,
                                                  String mediaType,
                                                  String staticFilePath) {
    if (HttpMethod.GET != requestWrapper.getHttpMethod()) {
      return new ResponseWrapper(mediaType, METHOD_NOT_ALLOWED.reasonPhrase().getBytes(), METHOD_NOT_ALLOWED);
    }

    if (isUriInvalid(requestWrapper.getPath())) {
      return fileNotFound(mediaType);
    }

    String uri = requestWrapper.getPath();

    if (uri.endsWith(PATH_SEPARATOR)) {
      uri += PATH_INDEX_HTML;
    }

    if (Strings.isNullOrEmpty(staticFilePath)) {
      return fromClasspath(requestWrapper, mediaType, PATH_THEME, uri);
    }

    if (staticFilePath.startsWith(PREFIX_CLASSPATH)) {
      return fromClasspath(requestWrapper, mediaType, staticFilePath.substring(PREFIX_CLASSPATH_LEN), uri);
    }

    return fromFileSystem(requestWrapper, mediaType, staticFilePath, uri);
  }


  private ResponseWrapper fromClasspath(RequestWrapper requestWrapper,
                                        String mediaType,
                                        String staticPath,
                                        String uri) {
    if (isModifiedSinceHeaderPresent(requestWrapper)) {
      return new ResponseWrapper(mediaType, NOT_MODIFIED);
    }

    String resource = staticPath + removeTopDir(uri);
    URL url = getClass().getResource(resource);
    if (null == url) {
      return fileNotFound(mediaType);
    }

    try {
      if (isDebugEnabled) {
        return new ResponseWrapper(mediaType, getContentAsByteArray(resource));

      } else {
        return new ResponseWrapper(mediaType, classpathStaticFileContentCache.get(resource));
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return new ResponseWrapper(MediaType.TEXT_PLAIN.getValue(), INTERNAL_SERVER_ERROR);
    }
  }

  private boolean isModifiedSinceHeaderPresent(RequestWrapper requestWrapper) {
    String ifModifiedSince = requestWrapper.getHeaders().get(HttpHeaderNames.IF_MODIFIED_SINCE);
    return !Strings.isNullOrEmpty(ifModifiedSince);
  }

  private ResponseWrapper fromFileSystem(RequestWrapper requestWrapper,
                                         String mediaType,
                                         String staticPath,
                                         String uri) {
    File file = new File(staticPath + removeTopDir(uri));
    if (isInvalidFile(file)) {
      return fileNotFound(mediaType);
    }

    if (isFileNotModified(requestWrapper, file)) {
      return new ResponseWrapper(mediaType, NOT_MODIFIED);
    }

    return new ResponseWrapper(mediaType, file);
  }

  private boolean isInvalidFile(File file) {
    return !isValidFile(file);
  }

  private boolean isValidFile(File file) {
    String path = file.getPath();
    int endIndex = path.indexOf(SIGN_EXCLAMATION);
    if (endIndex > 0) {
      int startIndex = path.indexOf(SIGN_COLON);
      return isValidFile(new File(path.substring(startIndex > 0 ? startIndex + 1 : 0, endIndex)));
    }
    return file.exists() && !file.isHidden() && !file.isDirectory() && file.isFile();
  }

  private boolean isUriInvalid(String uri) {
    if (uri.isEmpty() || uri.charAt(0) != SLASH) {
      return true;
    }

    uri = uri.replace(SLASH, File.separatorChar);

    return uri.contains(File.separator + DOT) ||
        uri.contains(DOT + File.separator) ||
        uri.charAt(0) == DOT || uri.charAt(uri.length() - 1) == DOT ||
        INSECURE_URI.matcher(uri).matches();
  }

  private byte[] getContentAsByteArray(String resource) throws IOException {
    try (InputStream input = getClass().getResourceAsStream(resource);
         ByteArrayOutputStream output = new ByteArrayOutputStream()
    ) {
      if (null == input) {
        return null;
      }

      FileUtils.copy(input, output);
      return output.toByteArray();
    }
  }

  private ResponseWrapper fileNotFound(String mediaType) {
    return new ResponseWrapper(mediaType, NOT_FOUND.reasonPhrase().getBytes(), NOT_FOUND);
  }

  private boolean isFileNotModified(RequestWrapper requestWrapper, File file) {
    String ifModifiedSince = requestWrapper.getHeaders().get(HttpHeaderNames.IF_MODIFIED_SINCE);
    if (Strings.isNullOrEmpty(ifModifiedSince)) {
      return false;
    }

    long ifModifiedSinceSeconds = DateTime.parse(ifModifiedSince, HTTP_DATE_FORMATTER).getMillis() / 1000;
    long fileLastModifiedSeconds = file.lastModified() / 1000;
    return ifModifiedSinceSeconds == fileLastModifiedSeconds;
  }

  private String removeTopDir(String uri) {
    int index = uri.indexOf(PATH_SEPARATOR, 1);
    if (0 < index && index < uri.length()) {
      return uri.substring(index);
    }

    return uri;
  }
}
