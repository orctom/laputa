package com.orctom.laputa.service.translator;

import com.orctom.laputa.service.annotation.Template;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.RequestMapping;

import java.util.regex.Pattern;

public abstract class TemplateResponseTranslator implements ResponseTranslator {

  protected static final String TEMPLATE_PREFIX = "/template";

  private static final MediaType TYPE = MediaType.TEXT_HTML;

  private static final Pattern BRACE_LEFT = Pattern.compile("\\{");
  private static final Pattern BRACE_RIGHT = Pattern.compile("}");
  private static final String EMPTY_STR = "";

  @Override
  public final String getMediaType() {
    return TYPE.getValue();
  }

  protected static String getTemplatePath(RequestMapping mapping) {
    Template template = mapping.getHandlerMethod().getJavaMethod().getAnnotation(Template.class);
    if (null != template) {
      return template.value();
    }

    return normalized(mapping.getUriPattern());
  }

  private static String normalized(String uriPattern) {
    return BRACE_RIGHT.matcher(
        BRACE_LEFT.matcher(uriPattern).replaceAll(EMPTY_STR)
    ).replaceAll(EMPTY_STR);
  }
}
