package com.orctom.laputa.service.translator.response;

import com.orctom.laputa.service.model.ResponseWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface ResponseTranslator {

  boolean fits(ResponseWrapper responseWrapper);

  void translate(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper);
}
