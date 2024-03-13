package com.mz_test.exception;

public final class IdNotMatchException extends RuntimeException{
    private static final String message = "해당 회원의 프로필이 아닙니다.";
    public IdNotMatchException() {
        super(message);
    }
}
