package com.orctom.laputa.service.translator.response;

import com.orctom.laputa.service.model.ResponseWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

public class ContentResponseTranslator extends AbstractResponseTranslator implements ResponseTranslator {

  public boolean fits(ResponseWrapper responseWrapper) {
//    return null != responseWrapper.getContent();
    return true;
  }

  @Override
  public void translate(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    FullHttpResponse res = createHttpResponse(responseWrapper);
    res.headers().set(HttpHeaderNames.CONTENT_TYPE, responseWrapper.getMediaType());
    setCookies(res, responseWrapper.getCookies());
    writeResponse(ctx, req, res, responseWrapper.getStatus());
  }
}
