package com.orctom.laputa.server;

import io.netty.handler.codec.http.DefaultHttpRequest;

/**
 * Filter / processor before the request be actually aprocessed
 * Created by hao on 6/24/16.
 */
public interface PreProcessor {

  void process(DefaultHttpRequest req);
}
