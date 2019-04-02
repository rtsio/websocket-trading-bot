package io.bux.assignment.client.exception;

public class BuxApiException extends Exception {

    public BuxApiException(String errorMessage) {
        super(errorMessage);
    }

    public BuxApiException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
