package com.reaper.launcher.lib;

public class ReaperLauncherException extends RuntimeException {

	private static final long serialVersionUID = 2847099853799385837L;

	public ReaperLauncherException() {
		super();
	}

	public ReaperLauncherException(String message) {
		super(message);
	}

	public ReaperLauncherException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReaperLauncherException(Throwable cause) {
		super(cause);
	}
}
