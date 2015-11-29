package com.orctom.laputa.server.encoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.orctom.laputa.server.MediaTypes;

import java.io.IOException;

/**
 * Encode data to xml
 * Created by hao on 11/25/15.
 */
public class XmlResponseEncoder implements ResponseEncoder {

	public static final String TYPE = MediaTypes.APPLICATION_XML;

	private static ObjectMapper mapper = new XmlMapper();

	@Override
	public byte[] encode(Object data) throws IOException {
		return mapper.writeValueAsBytes(data);
	}
}
