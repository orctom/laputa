package com.orctom.laputa.server.encoder;

import com.google.common.base.Splitter;
import com.orctom.laputa.server.MediaType;
import com.orctom.laputa.server.internal.ResponseTypeEncoder;
import com.orctom.laputa.server.model.Accepts;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import javax.swing.text.html.Option;
import java.util.*;

/**
 * ResponseEncoder registry
 * Created by hao on 11/25/15.
 */
public class ResponseEncoders {

	private static final Map<String, ResponseEncoder> ENCODERS = new HashMap<>();

	static {
		add(JsonResponseEncoder.TYPE.getValue(), new JsonResponseEncoder());
		add(XmlResponseEncoder.TYPE.getValue(), new XmlResponseEncoder());
	}

	public static void add(String type, ResponseEncoder encoder) {
		ENCODERS.put(type, encoder);
	}

	public static Optional<ResponseTypeEncoder> getEncoder(DefaultHttpRequest request) {
		String uri = request.getUri();
		if (uri.endsWith(".json")) {
			return getResponseTypeEncoder(MediaType.APPLICATION_JSON);
		}
		if (uri.endsWith(".xml")) {
			return getResponseTypeEncoder(MediaType.APPLICATION_XML);
		}
		if (uri.endsWith(".html")) {
			return getResponseTypeEncoder(MediaType.TEXT_HTML);
		}

		String accept = request.headers().get(HttpHeaders.Names.ACCEPT);
		List<String> accepts = new Accepts(accept).getAccepts();

		for (String type :accepts) {
			ResponseEncoder encoder = ENCODERS.get(type);
			if (null != encoder) {
				return Optional.of(new ResponseTypeEncoder(type, encoder));
			}
		}

		return Optional.empty();
	}

	private static Optional<ResponseTypeEncoder> getResponseTypeEncoder(MediaType mediaType) {
		return Optional.of(new ResponseTypeEncoder(mediaType, ENCODERS.get(mediaType.getValue())));
	}

	public static ResponseEncoder getDefaultEncoder() {
		return ENCODERS.get(JsonResponseEncoder.TYPE.getValue());
	}
}
