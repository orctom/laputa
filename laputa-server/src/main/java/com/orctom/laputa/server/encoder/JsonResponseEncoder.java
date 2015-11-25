package com.orctom.laputa.server.encoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orctom.laputa.server.MediaTypes;

import java.io.IOException;

/**
 * Encode data to json
 * Created by hao on 11/25/15.
 */
public class JsonResponseEncoder implements ResponseEncoder {

	public static final String TYPE = MediaTypes.APPLICATION_JSON;

	private static ObjectMapper mapper = new ObjectMapper();

	@Override
	public byte[] encode(Object data) throws IOException {
		return mapper.writeValueAsBytes(data);
	}
}
