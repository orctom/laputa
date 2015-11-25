package com.orctom.laputa.server.encoder;

import java.io.IOException;

/**
 * Encode response data:
 * From Java Object to html/json...
 * Created by hao on 11/25/15.
 */
public interface ResponseEncoder {

	byte[] encode(Object data) throws IOException;

}
