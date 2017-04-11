package com.orctom.laputa.service.translator.response;

import com.orctom.laputa.service.model.ResponseCookie;
import com.orctom.laputa.service.model.ResponseWrapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.orctom.laputa.service.Constants.*;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public abstract class AbstractResponseTranslator {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResponseTranslator.class);

  protected static final String CONTENT_TYPE = ".contentType";

  protected FullHttpResponse createHttpResponse(ResponseWrapper responseWrapper) {
    if (null == responseWrapper.getContent()) {
      return new DefaultFullHttpResponse(HTTP_1_1, responseWrapper.getStatus());
    }

    return new DefaultFullHttpResponse(
        HTTP_1_1,
        responseWrapper.getStatus(),
        Unpooled.wrappedBuffer(responseWrapper.getContent())
    );
  }

  protected void setCookies(FullHttpResponse res, Set<ResponseCookie> cookies) {
    if (null == cookies || cookies.isEmpty()) {
      return;
    }
    for (ResponseCookie cookie : cookies) {
      try {
        res.headers().set(SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
  }

  protected void writeResponse(ChannelHandlerContext ctx,
                               FullHttpRequest req,
                               FullHttpResponse res,
                               HttpResponseStatus status) {
    setDateHeader(req, res, status);
    if (!HttpUtil.isContentLengthSet(res)) {
      HttpUtil.setContentLength(res, res.content().readableBytes());
    }

    boolean keepAlive = HttpUtil.isKeepAlive(req);
    if (keepAlive) {
      res.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      ctx.write(res);
    } else {
      ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
    }
  }

  private void setDateHeader(FullHttpRequest req, FullHttpResponse res, HttpResponseStatus status) {
    String date = req.headers().get(IF_MODIFIED_SINCE);
    if (NOT_MODIFIED != status) {
      date = DateTime.now().toString(HTTP_DATE_FORMATTER);
    }
    res.headers().set(DATE, date);
    res.headers().set(LAST_MODIFIED, date);
  }

  protected void setNoCacheHeader(FullHttpResponse res) {
    res.headers().set(CACHE_CONTROL, HEADER_CACHE_CONTROL_NO_CACHE);
    res.headers().set(EXPIRES, HEADER_EXPIRE_NOW);
  }
}
