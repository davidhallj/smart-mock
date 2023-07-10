package com.davidhallj.smartmock.exception;

public class SmartMockException extends RuntimeException {

    public SmartMockException() {
    }

    public SmartMockException(String message) {
        super(message);
    }

    public SmartMockException(String message, Throwable cause) {
        super(message, cause);
    }

}
