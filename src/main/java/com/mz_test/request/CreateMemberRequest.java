package com.mz_test.request;

import com.mz_test.entity.Member;
import com.mz_test.response.PasswordResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateMemberRequest {

    @NotBlank(message = "이름을 입력해 주세요.")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디에는 공백이 포함될 수 없습니다.")
    @Size(min = 5, max = 18, message = "아이디는 5자 이상 18자 이하로 입력해 주십시오.")
    @NotBlank(message = "아이디를 입력해 주세요.")
    private String loginId;

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "패스워드에는 공백이 포함될 수 없습니다.")
    @Size(min = 8, message = "패스워드는 8자 이상 입력해 주십시오.")
    @NotBlank(message = "패스워드를 입력해 주세요.")
    private String password;

    @Valid
    private CreateProfileRequest createProfileRequest;

    public Member toEntity(PasswordResponse passwordResponse) {
        return Member.builder()
                .loginId(this.loginId)
                .salt(passwordResponse.getSalt())
                .password(passwordResponse.getPassword())
                .name(this.name)
                .build();
    }

    @Builder
    public CreateMemberRequest(String name, String loginId, String password, CreateProfileRequest createProfileRequest) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.createProfileRequest = createProfileRequest;
    }
}
