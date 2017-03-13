package com.orctom.laputa.service.processor;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Request Processor
 * Created by hao on 1/6/16.
 */
public interface RequestProcessor {

  boolean canHandleRequest(RequestWrapper requestWrapper);

  ResponseWrapper handleRequest(RequestWrapper requestWrapper, String mediaType);

}
