package com.mz_test.exception;

public class MemberNotFoundException extends NotFoundException {

    private static final String message = "회원[%s] 아이디는 존재하지 않습니다.";
    private static final String defaultMessage = "회원이 존재하지 않습니다";
    public MemberNotFoundException(final Long memberId) {
        super(String.format(message, memberId));
    }

    public MemberNotFoundException() {
        super(String.format(defaultMessage));
    }


}
