package com.orctom.laputa.server.processor;

import com.orctom.laputa.server.model.Response;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Request Processor
 * Created by hao on 1/6/16.
 */
public interface RequestProcessor {

  Response handleRequest(HttpRequest req);

}
