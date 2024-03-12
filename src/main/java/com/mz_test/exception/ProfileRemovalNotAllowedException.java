package com.mz_test.exception;

public class ProfileRemovalNotAllowedException extends NotAllowedException{
    private static final String message = "프로필은 최소 한개 가지고 있어야 합니다.";

    public ProfileRemovalNotAllowedException() {
        super(message);
    }

}
