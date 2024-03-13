package com.mz_test.exception;


public class NotAllowedException extends RuntimeException {

    public NotAllowedException(final String message) {
        super(message);
    }
}