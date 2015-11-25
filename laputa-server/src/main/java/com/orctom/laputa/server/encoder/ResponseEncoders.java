package com.orctom.laputa.server.encoder;

import java.util.HashMap;
import java.util.Map;

/**
 * ResponseEncoder registry
 * Created by hao on 11/25/15.
 */
public class ResponseEncoders {

	private static final Map<String, ResponseEncoder> ENCODERS = new HashMap<>();

	static {
		add(JsonResponseEncoder.TYPE, new JsonResponseEncoder());
	}

	public static void add(String type, ResponseEncoder encoder) {
		ENCODERS.put(type, encoder);
	}

	public static ResponseEncoder getEncoder(String type) {
		ResponseEncoder encoder = ENCODERS.get(type);
		if (null != encoder) {
			return encoder;
		}

		return ENCODERS.get(JsonResponseEncoder.TYPE);
	}
}
