package com.orctom.laputa.service.translator.response;

import com.orctom.laputa.service.model.ResponseWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import static io.netty.handler.codec.http.HttpHeaderNames.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.MOVED_PERMANENTLY;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RedirectResponseTranslator extends AbstractResponseTranslator implements ResponseTranslator {

  @Override
  public boolean fits(ResponseWrapper responseWrapper) {
    String redirectTo = responseWrapper.getRedirectTo();
    return null != redirectTo && redirectTo.trim().length() > 0;
  }

  @Override
  public void translate(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    HttpResponseStatus status = responseWrapper.isPermanentRedirect() ? MOVED_PERMANENTLY : FOUND;
    FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, status);
    res.headers().set(LOCATION, responseWrapper.getRedirectTo());
    setNoCacheHeader(res);
    setCookies(res, responseWrapper.getCookies());
    writeResponse(ctx, req, res, responseWrapper.getStatus());
  }
}
