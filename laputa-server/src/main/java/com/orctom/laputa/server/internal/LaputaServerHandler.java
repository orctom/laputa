package com.orctom.laputa.server.internal;

import com.orctom.laputa.server.model.Response;
import com.orctom.laputa.server.processor.RequestProcessor;
import com.orctom.laputa.server.processor.impl.DefaultRequestProcessor;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class LaputaServerHandler extends ChannelInboundHandlerAdapter {

	private RequestProcessor requestProcessor = new DefaultRequestProcessor();

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof DefaultHttpRequest) {
			DefaultHttpRequest req = (DefaultHttpRequest) msg;

			if (HttpHeaders.is100ContinueExpected(req)) {
				ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
			}

			boolean keepAlive = HttpHeaders.isKeepAlive(req);

			Response response = requestProcessor.handleRequest(req);

			FullHttpResponse res = new DefaultFullHttpResponse(
					HTTP_1_1, OK, Unpooled.wrappedBuffer(response.getContent()));
			res.headers().set(CONTENT_TYPE, response.getMediaType());
			res.headers().set(CONTENT_LENGTH, res.content().readableBytes());

			if (!keepAlive) {
				ctx.write(res).addListener(ChannelFutureListener.CLOSE);
			} else {
				res.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
				ctx.write(res);
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}