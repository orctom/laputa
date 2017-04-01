package com.orctom.laputa.service.shiro.processor;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.processor.PreProcessor;
import org.springframework.stereotype.Component;

@Component
public class ShiroProcessor implements PreProcessor {

  @Override
  public int getOrder() {
    return 0;
  }

  @Override
  public void process(RequestWrapper requestWrapper, Context ctx) {
  }
}
