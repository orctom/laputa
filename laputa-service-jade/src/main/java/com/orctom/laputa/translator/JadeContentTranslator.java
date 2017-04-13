package com.orctom.laputa.translator;

import com.orctom.laputa.exception.IllegalConfigException;
import com.orctom.laputa.service.exception.TemplateProcessingException;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.service.translator.content.TemplateContentTranslator;
import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class JadeContentTranslator extends TemplateContentTranslator<JadeTemplate> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JadeContentTranslator.class);

  private static final String TEMPLATE_SUFFIX = ".jade";

  @Override
  public byte[] translate(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) throws IOException {
    try {
      JadeTemplate template = getTemplate(requestWrapper, responseWrapper);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Writer writer = new BufferedWriter(new OutputStreamWriter(out));
      Map<String, Object> model = getModel(responseWrapper);
      template.process(new JadeModel(model), writer);
      return out.toByteArray();
    } catch (ExecutionException e) {
      throw new TemplateProcessingException(e.getMessage(), e);
    }
  }

  protected JadeTemplate getTemplate0(String template) {
    try {
      String templatePath = template + TEMPLATE_SUFFIX;
      return Jade4J.getTemplate(templatePath);
    } catch (IOException e) {
      throw new IllegalConfigException(e.getMessage());
    }
  }
}
