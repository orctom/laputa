package com.orctom.laputa.translator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.orctom.laputa.exception.IllegalConfigException;
import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.exception.TemplateProcessingException;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestMapping;
import com.orctom.laputa.service.translator.TemplateResponseTranslator;
import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class JadeResponseTranslator extends TemplateResponseTranslator {

  private static final Logger LOGGER = LoggerFactory.getLogger(JadeResponseTranslator.class);

  private static final String TEMPLATE_SUFFIX = ".jade";

  private static final boolean isDebugEnabled = Configurator.getInstance().isDebugEnabled();

  private static LoadingCache<RequestMapping, JadeTemplate> templates = CacheBuilder.newBuilder()
      .build(
          new CacheLoader<RequestMapping, JadeTemplate>() {
            @Override
            public JadeTemplate load(RequestMapping mapping) throws Exception {
              return getTemplate0(mapping);
            }
          }
      );

  @Override
  public byte[] translate(RequestMapping mapping, Object data, Context ctx) throws IOException {
    try {
      JadeTemplate template = getTemplate(mapping);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Writer writer = new BufferedWriter(new OutputStreamWriter(out));
      Map<String, Object> model = new HashMap<>(ctx.getData());
      model.put("model", data);
      template.process(new JadeModel(model), writer);
      return out.toByteArray();
    } catch (ExecutionException e) {
      throw new TemplateProcessingException(e.getMessage());
    }
  }

  private JadeTemplate getTemplate(RequestMapping mapping) throws ExecutionException {
    if (isDebugEnabled) {
      return getTemplate0(mapping);
    }

    return templates.get(mapping);
  }

  private static JadeTemplate getTemplate0(RequestMapping mapping) {
    try {
      String templatePath = getTemplatePath(mapping) + TEMPLATE_SUFFIX;
      LOGGER.debug("Template Path: {} for url: {}", templatePath, mapping.getUriPattern());
      return Jade4J.getTemplate(templatePath);
    } catch (IOException e) {
      throw new IllegalConfigException(e.getMessage());
    }
  }
}
