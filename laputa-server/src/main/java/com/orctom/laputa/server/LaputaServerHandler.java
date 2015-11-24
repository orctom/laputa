package com.orctom.laputa.server;

import com.orctom.laputa.server.config.HTTPMethod;
import com.orctom.laputa.server.config.Handler;
import com.orctom.laputa.server.config.MappingConfig;
import com.orctom.laputa.util.ClassUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.Mapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class LaputaServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOGGER = LogManager.getLogger();

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
			uri = removeHashFromUri(uri);

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

			byte[] content = {'5', '0', '0'};
			try {
				Object data = handler.process(uri);
				content = encode(req, handler.getHandlerMethod().getReturnType(), data);
			} catch (Throwable e) {
				LOGGER.error(e);
			}

			FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
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

	private byte[] encode(HttpRequest req, Class<?> returnType, Object data) {
		if (ClassUtils.isSimpleValueType(returnType)) {
			return ("{'message': '" + String.valueOf(data) + "'}").getBytes();
		}
		return "{'this is some data'}".getBytes();
	}

	private String removeHashFromUri(String uri) {
		int hashIndex = uri.indexOf("#");
		if (hashIndex > 0) {
			return uri.substring(0, hashIndex);
		} else {
			return uri;
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
