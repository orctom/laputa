package com.orctom.laputa.server.processor;

import com.orctom.laputa.server.example.model.Response;
import io.netty.handler.codec.http.DefaultHttpRequest;

/**
 * Request Processor
 * Created by hao on 1/6/16.
 */
public interface RequestProcessor {

  Response handleRequest(DefaultHttpRequest req);

}
