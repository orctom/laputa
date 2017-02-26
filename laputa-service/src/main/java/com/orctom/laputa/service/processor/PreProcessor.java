package com.orctom.laputa.service.processor;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;

/**
 * Filter / processor before the request be actually aprocessed
 * Created by hao on 6/24/16.
 */
public interface PreProcessor {

  void process(RequestWrapper requestWrapper, Context ctx);
}
