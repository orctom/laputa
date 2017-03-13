package com.orctom.laputa.service.handler;

import com.orctom.laputa.service.exception.RequestProcessingException;
import com.orctom.laputa.service.model.MediaType;
import com.orctom.laputa.service.model.ResponseWrapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class WebHandler extends AbstractServiceHandler implements ServiceHandler {

  @Override
  public boolean handle(ChannelHandlerContext ctx, FullHttpRequest req, ResponseWrapper responseWrapper) {
    if (isNotStaticFileResponse(responseWrapper)) {
      return false;
    }

    try {
      RandomAccessFile file;
      try {
        file = new RandomAccessFile(responseWrapper.getFile(), "r");
      } catch (FileNotFoundException ignore) {
        ignore.printStackTrace();
        sendError(ctx, req, new ResponseWrapper(MediaType.TEXT_PLAIN.getValue(), NOT_FOUND));
        return true;
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
      throw new RequestProcessingException(e.getMessage(), e);
    }
    return true;
  }

  private boolean isNotStaticFileResponse(ResponseWrapper responseWrapper) {
    return null == responseWrapper.getFile();
  }
}
