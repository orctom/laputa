package com.orctom.laputa.server;

import com.orctom.laputa.server.config.HTTPMethod;
import com.orctom.laputa.server.config.Handler;
import com.orctom.laputa.server.config.MappingConfig;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.Mapping;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class LaputaServerHandler extends ChannelInboundHandlerAdapter {

	private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;

			if (HttpHeaders.is100ContinueExpected(req)) {
				ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
			}
			boolean keepAlive = HttpHeaders.isKeepAlive(req);
			HttpMethod method = req.getMethod();
			String uri = req.getUri();

			// remove hash
			uri = uri.substring(0, uri.indexOf("#"));

			// get query string
			int questionMarkIndex = uri.indexOf("?");
			String queryStr = null;
			if (questionMarkIndex > 0) {
				queryStr = uri.substring(questionMarkIndex + 1);
				uri = uri.substring(0, questionMarkIndex);
			}
			System.out.println("uri      = " + uri);
			System.out.println("queryStr = " + queryStr);

			Handler handler = MappingConfig.getInstance().getHandler(uri, getHttpMethod(method));

			Object data = handler.process(uri);


			FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
			res.headers().set(CONTENT_TYPE, "text/plain");
			res.headers().set(CONTENT_LENGTH, res.content().readableBytes());

			if (!keepAlive) {
				ctx.write(res).addListener(ChannelFutureListener.CLOSE);
			} else {
				res.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
				ctx.write(res);
			}
		}
	}

	private HTTPMethod getHttpMethod(HttpMethod method) {
		if (HttpMethod.DELETE == method) {
			return HTTPMethod.DELETE;
		}
		if (HttpMethod.HEAD == method) {
			return HTTPMethod.HEAD;
		}
		if (HttpMethod.OPTIONS == method) {
			return HTTPMethod.OPTIONS;
		}
		if (HttpMethod.POST == method) {
			return HTTPMethod.POST;
		}
		if (HttpMethod.PUT == method) {
			return HTTPMethod.PUT;
		}
		return HTTPMethod.GET;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
