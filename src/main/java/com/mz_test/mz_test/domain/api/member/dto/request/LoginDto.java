package com.mz_test.mz_test.domain.api.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

    @NotBlank(message = "아이디를 입력해 주십시오.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요.")
    @Size(min = 5,max = 18, message = "아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요.")
    private String id;

    @NotBlank(message = "패스워드를 입력해 주십시오.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요.")
    @Size(min = 8, message = "아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요.")
    private String password;
}
