package com.mz_test.exception;

public class ProfileNotFoundException extends NotFoundException {

    private static final String message = "프로필[%s]을 찾을수 없습니다.";

    public ProfileNotFoundException(final Long profileId) {
        super(String.format(message, profileId));
    }


}
