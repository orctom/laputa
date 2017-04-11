package com.orctom.laputa.service.translator.response;

import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.typesafe.config.Config;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.orctom.laputa.service.Constants.CFG_STATIC_FILE_CACHE;
import static com.orctom.laputa.service.Constants.HTTP_DATE_FORMATTER;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class FileResponseTranslator extends AbstractResponseTranslator implements ResponseTranslator {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileResponseTranslator.class);

  private static int staticFileCache;

  static {
    Config config = Configurator.getInstance().getConfig();
    staticFileCache = config.getInt(CFG_STATIC_FILE_CACHE);
  }

  @Override
  public boolean fits(ResponseWrapper responseWrapper) {
    return null != responseWrapper.getFile();
  }

  @Override
  public void translate(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    try {
      RandomAccessFile file;
      try {
        file = new RandomAccessFile(responseWrapper.getFile(), "r");
      } catch (FileNotFoundException ignore) {
        ignore.printStackTrace();
        sendError(ctx, req, new ResponseWrapper(MediaType.TEXT_PLAIN.getValue(), NOT_FOUND));
        return;
      }

      HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
      long contentLength = file.length();
      res.headers().set(CONTENT_LENGTH, contentLength);
      res.headers().set(CONTENT_TYPE, responseWrapper.getMediaType());
      setDateAndCacheHeaders(res, responseWrapper.getFile());
      boolean keepAlive = HttpUtil.isKeepAlive(req);
      if (keepAlive) {
        res.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      }

      ctx.write(res);

      ChannelFuture lastContentFuture;
      if (null == ctx.pipeline().get(SslHandler.class)) {
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, contentLength), ctx.newProgressivePromise());
        lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

      } else {
        lastContentFuture =
            ctx.writeAndFlush(
                new HttpChunkedInput(new ChunkedFile(file, 0, contentLength, 8192)),
                ctx.newProgressivePromise()
            );
      }

      if (!keepAlive) {
        lastContentFuture.addListener(ChannelFutureListener.CLOSE);
      }

    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
      responseWrapper.setStatus(INTERNAL_SERVER_ERROR);
      responseWrapper.setContent(INTERNAL_SERVER_ERROR.reasonPhrase().getBytes());
      sendError(ctx, req, responseWrapper);
    }
  }

  private void sendError(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    FullHttpResponse res = createHttpResponse(responseWrapper);
    res.headers().set(HttpHeaderNames.CONTENT_TYPE, MediaType.TEXT_PLAIN.getValue());
    setNoCacheHeader(res);
    writeResponse(ctx, req, res, responseWrapper.getStatus());
  }

  private void setDateAndCacheHeaders(HttpResponse res, File file) {
    DateTime now = DateTime.now();
    res.headers().set(DATE, now.toString(HTTP_DATE_FORMATTER));
    res.headers().set(LAST_MODIFIED, new DateTime(file.lastModified()).toString(HTTP_DATE_FORMATTER));
    res.headers().set(EXPIRES, now.plusSeconds(staticFileCache).toString(HTTP_DATE_FORMATTER));
    res.headers().set(CACHE_CONTROL, "public, max-age=" + staticFileCache);
  }
}
