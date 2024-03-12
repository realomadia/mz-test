package com.mz_test.exception;

import lombok.Getter;

@Getter
public class NotAllowedException extends RuntimeException {

    public NotAllowedException(final String message) {
        super(message);
    }
}