package com.orctom.laputa.util.exception;

/**
 * Created by hao-chen2 on 1/5/2015.
 */
public class ClassLoadingException extends Exception {

	public ClassLoadingException(String message) {
		super(message);
	}

	public ClassLoadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClassLoadingException(Throwable cause) {
		super(cause);
	}

	protected ClassLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
