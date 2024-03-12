package com.mz_test.exception;

import lombok.Getter;

@Getter
public class DuplicateException extends RuntimeException {

    public DuplicateException(final String message) {
        super(message);
    }
}