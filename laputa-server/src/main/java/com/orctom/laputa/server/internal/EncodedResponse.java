package com.orctom.laputa.server.internal;

import com.orctom.laputa.server.MediaType;

/**
 * Encoded response
 * Created by hao on 11/30/15.
 */
public class EncodedResponse {

	private String mediaType;
	private byte[] content;

	public EncodedResponse(String mediaType, byte[] content) {
		this.content = content;
		this.mediaType = mediaType;
	}

	public byte[] getContent() {
		return content;
	}

	public String getMediaType() {
		return mediaType;
	}
}
