package com.mz_test.mz_test.global.config.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // SIGN UP
    SIGN_UP_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 가입된 아이디 입니다. 다른 아이디를 입력하여 주세요."),
    REQUIRED_PROFILE(HttpStatus.BAD_REQUEST, "프로필은 최소 한 개 이상이어야 합니다."),

    // SIGN IN
    SIGN_IN_FAILED(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요."),
    HMAC_SHA256_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "HMAC SHA-256 서명을 생성하지 못했습니다."),
    // Common
    REQUIRED_REQUEST_NO_PARAMETER(HttpStatus.BAD_REQUEST, "필수 값이 존재하지 않습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력 값이 잘못 되었습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "메소드를 사용할 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생하였습니다. 관리자에게 문의 부탁드립니다."),
    ARBITRARY_DATA_INPUT_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "임의의 데이터가 입력되어 서버에서 문제가 발생하였습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "입력 값 타입이 잘못되었습니다."),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 기능입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "재로그인 해주세요."),
    PROFILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "프로필을 찾을수 없습니다."),
    PROFILE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "프로필은 최소 한개 가지고 있어야 합니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "회원을 찾을수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
