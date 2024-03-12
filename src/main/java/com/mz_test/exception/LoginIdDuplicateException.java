package com.mz_test.exception;

public class LoginIdDuplicateException extends DuplicateException {

    private static final String message = "아이디 [%s]는 중복된 아이디 입니다.";

    public LoginIdDuplicateException(final String loginId) {
        super(String.format(message, loginId));
    }
}
