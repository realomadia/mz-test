package com.mz_test.exception;

public class DuplicateException extends RuntimeException {

    public DuplicateException(final String message) {
        super(message);
    }
}