package io.bux.assignment.exception;

public class IncorrectPriceRangeException extends Exception {

    public IncorrectPriceRangeException(String errorMessage) {
        super(errorMessage);
    }
}