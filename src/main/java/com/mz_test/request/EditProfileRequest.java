package com.mz_test.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileRequest {

    @NotBlank(message = "별명을 입력해 주십시오.")
    private String nickname;

    @NotBlank(message = "휴대폰 번호를 입력해 주십시오.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대폰 번호를 재확인 해주십시오.")
    private String phone;

    private String address;

    @Builder
    public EditProfileRequest(String nickname, String phone, String address) {
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
    }
}
