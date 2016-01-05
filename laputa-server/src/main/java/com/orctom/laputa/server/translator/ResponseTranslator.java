package com.orctom.laputa.server.translator;

import java.io.IOException;

/**
 * Encode response data:
 * From Java Object to html/json...
 * Created by hao on 11/25/15.
 */
public interface ResponseTranslator {

	String getMediaType();

	byte[] translate(Object data) throws IOException;

}
