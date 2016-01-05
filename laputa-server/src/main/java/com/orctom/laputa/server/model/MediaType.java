package com.orctom.laputa.server.model;


/**
 * media types
 * Created by hao on 11/26/15.
 */
public enum MediaType {

	TEXT_HTML("text/html"),
	APPLICATION_JSON("application/json"),
	APPLICATION_XML("application/xml");

	private String value;

	private MediaType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
