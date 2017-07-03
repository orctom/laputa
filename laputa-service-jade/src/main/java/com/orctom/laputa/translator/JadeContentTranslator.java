package com.orctom.laputa.translator;

import com.orctom.laputa.exception.IllegalConfigException;
import com.orctom.laputa.service.exception.TemplateProcessingException;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.translator.content.TemplateContentTranslator;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.ClasspathTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.orctom.laputa.service.Constants.PATH_SEPARATOR;

public class JadeContentTranslator extends TemplateContentTranslator<JadeTemplate> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JadeContentTranslator.class);

  private static final String TEMPLATE_SUFFIX = ".jade";

  @Override
  public byte[] translate(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) throws IOException {
    try {
      JadeTemplate template = getTemplate(requestWrapper, responseWrapper);
      StringWriter writer = new StringWriter();
      Map<String, Object> model = getModel(responseWrapper);
      template.process(new JadeModel(model), writer);

      return writer.toString().getBytes();
    } catch (ExecutionException e) {
      throw new TemplateProcessingException(e.getMessage(), e);
    }
  }

  protected JadeTemplate getTemplate0(String template) {
    try {
      String templatePath = TEMPLATE_PREFIX + PATH_SEPARATOR + template + TEMPLATE_SUFFIX;
      return getJadeTemplate(templatePath);

    } catch (IOException e) {
      throw new IllegalConfigException(e.getMessage());
    }
  }

  private JadeTemplate getJadeTemplate(String templatePath) throws IOException {
    JadeConfiguration config = new JadeConfiguration();
    if (isDebugEnabled) {
      config.clearCache();
      config.setCaching(false);
    }
    config.setTemplateLoader(new ClasspathTemplateLoader());
    return config.getTemplate(templatePath);
  }
}
