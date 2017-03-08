package com.orctom.laputa.service.processor;

import com.orctom.laputa.service.model.ResponseWrapper;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Request Processor
 * Created by hao on 1/6/16.
 */
@FunctionalInterface
public interface RequestProcessor {

  ResponseWrapper handleRequest(FullHttpRequest req);

}
