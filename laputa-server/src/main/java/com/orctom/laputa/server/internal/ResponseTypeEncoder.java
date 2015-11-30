package com.orctom.laputa.server.internal;

import com.orctom.laputa.server.MediaType;
import com.orctom.laputa.server.encoder.ResponseEncoder;

/**
 * MediaType and Encoder
 * Created by hao on 11/30/15.
 */
public class ResponseTypeEncoder {

	private String responseType;
	private ResponseEncoder encoder;

	public ResponseTypeEncoder(MediaType responseType, ResponseEncoder encoder) {
		this.responseType = responseType.getValue();
		this.encoder = encoder;
	}

	public ResponseTypeEncoder(String responseType, ResponseEncoder encoder) {
		this.responseType = responseType;
		this.encoder = encoder;
	}

	public ResponseEncoder getEncoder() {
		return encoder;
	}

	public String getResponseType() {
		return responseType;
	}
}
