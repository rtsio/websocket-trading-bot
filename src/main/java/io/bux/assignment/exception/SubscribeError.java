package io.bux.assignment.exception;

public class SubscribeError extends Exception {

    public SubscribeError(String errorMessage) {
        super(errorMessage);
    }

    public SubscribeError(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
