package com.orctom.laputa.server.model;

/**
 * Encoded response
 * Created by hao on 11/30/15.
 */
public class Response {

	private String mediaType;
	private byte[] content;

	public Response(String mediaType, byte[] content) {
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
