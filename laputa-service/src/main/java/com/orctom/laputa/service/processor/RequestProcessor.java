package com.orctom.laputa.service.processor;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;

/**
 * Request Processor
 * Created by hao on 1/6/16.
 */
public interface RequestProcessor {

  default int getOrder() {
    return 0;
  }

  void handleRequest(final RequestWrapper requestWrapper, final ResponseWrapper responseWrapper);

}
