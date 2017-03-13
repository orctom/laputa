package com.orctom.laputa.service.handler;

import com.orctom.laputa.service.model.ResponseWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface ServiceHandler {

  boolean handle(ChannelHandlerContext ctx,
                 FullHttpRequest req,
                 ResponseWrapper responseWrapper);

}
