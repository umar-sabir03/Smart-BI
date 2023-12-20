package com.pilog.mdm.exception;

public class RegistrationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RegistrationException() {
		super();
	}

	public RegistrationException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RegistrationException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegistrationException(String message) {
		super(message);
	}

	public RegistrationException(Throwable cause) {
		super(cause);
	}

}
