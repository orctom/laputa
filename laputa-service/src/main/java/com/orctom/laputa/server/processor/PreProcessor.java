package com.orctom.laputa.server.processor;

import com.orctom.laputa.server.model.RequestWrapper;

/**
 * Filter / processor before the request be actually aprocessed
 * Created by hao on 6/24/16.
 */
public interface PreProcessor {

  void process(RequestWrapper requestWrapper);
}
