package com.orctom.laputa.server.config;

public enum HTTPMethod {

	GET("@get"),
	POST("@post"),
	PUT("@put"),
	DELETE("@delete"),
	HEAD("@head"),
	OPTIONS("@options");

	private String key;

	private HTTPMethod(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
