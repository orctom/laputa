package com.orctom.laputa.server.internal;

import com.google.common.base.Strings;
import com.orctom.laputa.server.MediaTypes;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Resolve requested content type
 * Created by hao on 11/25/15.
 */
public class ContentTypeResolver {

	public static String resolve(HttpRequest req) {
		DefaultHttpRequest request = (DefaultHttpRequest) req;

		String uri = request.getUri();
		if (uri.endsWith(".json")) {
			return MediaTypes.APPLICATION_JSON;
		}
		if (uri.endsWith(".xml")) {
			return MediaTypes.APPLICATION_XML;
		}
		if (uri.endsWith(".html")) {
			return MediaTypes.TEXT_HTML;
		}

		String accept = request.headers().get("Accept");
		if (!Strings.isNullOrEmpty(accept)) {
			if (accept.contains("json")) {
				return MediaTypes.APPLICATION_JSON;
			}
			if (accept.contains("xml")) {
				return MediaTypes.APPLICATION_XML;
			}
			if (accept.contains("html")) {
				return MediaTypes.TEXT_HTML;
			}
		}

		return MediaTypes.APPLICATION_JSON;
	}
}
