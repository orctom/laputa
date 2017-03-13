package com.orctom.laputa.service.handler;

import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.typesafe.config.Config;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.joda.time.DateTime;

import java.io.File;

import static com.orctom.laputa.service.Constants.*;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderNames.CACHE_CONTROL;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public abstract class AbstractServiceHandler {

  private static int staticFileCache;

  static {
    Config config = Configurator.getInstance().getConfig();
    staticFileCache = config.getInt(CFG_STATIC_FILE_CACHE);
  }

  protected void sendError(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    FullHttpResponse res = getHttpResponse(responseWrapper);
    res.headers().set(HttpHeaderNames.CONTENT_TYPE, MediaType.TEXT_PLAIN.getValue());
    setNoCacheHeader(res);
    writeResponse(ctx, req, res, responseWrapper.getStatus());
  }

  protected FullHttpResponse getHttpResponse(ResponseWrapper responseWrapper) {
    if (null == responseWrapper.getContent()) {
      return new DefaultFullHttpResponse(HTTP_1_1, responseWrapper.getStatus());
    }

    return new DefaultFullHttpResponse(
        HTTP_1_1,
        responseWrapper.getStatus(),
        Unpooled.wrappedBuffer(responseWrapper.getContent())
    );
  }

  protected void setNoCacheHeader(FullHttpResponse res) {
    res.headers().set(CACHE_CONTROL, HEADER_CACHE_CONTROL_NO_CACHE);
    res.headers().set(EXPIRES, HEADER_EXPIRE_NOW);
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

  protected void setDateHeader(FullHttpRequest req, FullHttpResponse res, HttpResponseStatus status) {
    String date = req.headers().get(IF_MODIFIED_SINCE);
    if (NOT_MODIFIED != status) {
      date = DateTime.now().toString(HTTP_DATE_FORMATTER);
    }
    res.headers().set(DATE, date);
    res.headers().set(LAST_MODIFIED, date);
  }

  protected void setDateAndCacheHeaders(HttpResponse res, File file) {
    DateTime now = DateTime.now();
    res.headers().set(DATE, now.toString(HTTP_DATE_FORMATTER));
    res.headers().set(LAST_MODIFIED, new DateTime(file.lastModified()).toString(HTTP_DATE_FORMATTER));
    res.headers().set(EXPIRES, now.plusSeconds(staticFileCache).toString(HTTP_DATE_FORMATTER));
    res.headers().set(CACHE_CONTROL, "public, max-age=" + staticFileCache);
  }
}
