package com.orctom.laputa.translator;

import com.orctom.laputa.exception.IllegalConfigException;
import com.orctom.laputa.service.exception.TemplateProcessingException;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.translator.content.TemplateContentTranslator;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import static com.orctom.laputa.service.Constants.PATH_SEPARATOR;

/**
 * Freemarker web page renderer
 * Created by hao on 2/19/17.
 */
public class FreemarkerContentTranslator extends TemplateContentTranslator<Template> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerContentTranslator.class);

  private static final String TEMPLATE_SUFFIX = ".ftl";

  private static final Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

  public FreemarkerContentTranslator() {
    cfg.setClassForTemplateLoading(FreemarkerContentTranslator.class, PATH_SEPARATOR + TEMPLATE_PREFIX);
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);

    if (isDebugEnabled) {
      cfg.setTemplateUpdateDelayMilliseconds(200);
    }
  }

  @Override
  public byte[] translate(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) throws IOException {
    try {
      Template template = getTemplate(requestWrapper, responseWrapper);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Map<String, Object> model = getModel(responseWrapper);
      try (Writer writer = new OutputStreamWriter(out)) {
        template.process(model, writer);
      }
      return out.toByteArray();
    } catch (Exception e) {
      throw new TemplateProcessingException(e.getMessage(), e);
    }
  }

  protected Template getTemplate0(String template) {
    try {
      String templatePath = template + TEMPLATE_SUFFIX;
      return cfg.getTemplate(templatePath);
    } catch (Exception e) {
      throw new IllegalConfigException(e.getMessage());
    }
  }
}
