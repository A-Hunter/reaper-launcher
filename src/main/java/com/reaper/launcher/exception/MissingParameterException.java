package com.reaper.launcher.exception;

public class MissingParameterException extends RuntimeException {

	private static final long serialVersionUID = 2847099853799385837L;

	public MissingParameterException() {
		super();
	}

	public MissingParameterException(String message) {
		super(message);
	}

	public MissingParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingParameterException(Throwable cause) {
		super(cause);
	}
}
