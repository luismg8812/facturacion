package com.fact.api;

public class FactException extends RuntimeException{

	private static final long serialVersionUID = -4958008163896284972L;

	public FactException() {
		super();
	}

	public FactException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FactException(String message, Throwable cause) {
		super(message, cause);
	}

	public FactException(String message) {
		super(message);
	}

	public FactException(Throwable cause) {
		super(cause);
	}

	
	
}
