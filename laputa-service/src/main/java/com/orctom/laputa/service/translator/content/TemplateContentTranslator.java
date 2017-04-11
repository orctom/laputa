package com.orctom.laputa.service.translator.content;

import com.google.common.base.Strings;
import com.orctom.laputa.service.annotation.Template;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.RequestMapping;

import java.util.regex.Pattern;

import static com.orctom.laputa.service.Constants.PATH_INDEX;
import static com.orctom.laputa.service.Constants.PATH_SEPARATOR;

public abstract class TemplateContentTranslator implements ContentTranslator {

  protected static final String TEMPLATE_PREFIX = "/template";

  private static final MediaType TYPE = MediaType.TEXT_HTML;

  private static final Pattern BRACE_LEFT = Pattern.compile("\\{");
  private static final Pattern BRACE_RIGHT = Pattern.compile("}");
  private static final String EMPTY_STR = "";

  @Override
  public final String getMediaType() {
    return TYPE.getValue();
  }

  @Override
  public final String getExtension() {
    return TYPE.getExtension();
  }

  protected static String getTemplatePath(RequestMapping mapping) {
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
}
