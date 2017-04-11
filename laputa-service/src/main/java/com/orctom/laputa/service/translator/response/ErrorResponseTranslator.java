package com.orctom.laputa.service.translator.response;

import com.orctom.laputa.service.model.ResponseWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

import static com.orctom.laputa.service.model.MediaType.TEXT_HTML;
import static com.orctom.laputa.service.model.MediaType.TEXT_PLAIN;

public class ErrorResponseTranslator extends AbstractResponseTranslator implements ResponseTranslator {

  @Override
  public boolean fits(ResponseWrapper responseWrapper) {
    return responseWrapper.getStatus().code() >= 400;
  }

  @Override
  public void translate(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    FullHttpResponse res = createHttpResponse(responseWrapper);
    String mediaType = TEXT_HTML.getValue().equals(responseWrapper.getMediaType()) ?
        TEXT_HTML.getValue() : TEXT_PLAIN.getValue();
    res.headers().set(HttpHeaderNames.CONTENT_TYPE, mediaType);
    setNoCacheHeader(res);
    setCookies(res, responseWrapper.getCookies());
    writeResponse(ctx, req, res, responseWrapper.getStatus());
  }
}
