package com.monitoring.exception;

public class DataCollectionException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public DataCollectionException(String message) {
		super(message);
	}

	public DataCollectionException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
