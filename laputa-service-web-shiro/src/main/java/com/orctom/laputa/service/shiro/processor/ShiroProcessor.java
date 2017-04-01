package com.orctom.laputa.service.shiro.processor;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.processor.PreProcessor;
import com.orctom.laputa.service.shiro.ShiroContext;
import com.orctom.laputa.service.shiro.filter.Filter;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniFactorySupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class Authenticator implements PreProcessor {

  private Map<String, List<Filter>> filterMap = ShiroContext.getInstance().getFilters();

  @Override
  public int getOrder() {
    return 0;
  }

  @Override
  public void process(RequestWrapper requestWrapper, Context ctx) {
    if (filterMap.isEmpty()) {
      return;
    }

    String path = requestWrapper.getPath();
    for (Map.Entry<String, List<Filter>> entry : filterMap.entrySet()) {
      String pattern = entry.getKey();
      List<Filter> filters = entry.getValue();

    }
  }
}
