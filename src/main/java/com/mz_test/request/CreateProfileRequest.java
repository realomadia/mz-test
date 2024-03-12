package com.mz_test.request;

import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProfileRequest {

    @NotBlank(message = "별명을 입력해 주십시오.")
    private String nickname;

    @NotBlank(message = "휴대폰 번호를 입력해 주십시오.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대폰 번호를 재확인 해주십시오.")
    private String phone;

    private String address;

    public Profile toEntity(Member member, int isMain) {
        return Profile.builder()
                .member(member)
                .phone(this.phone)
                .address(this.address)
                .nickname(this.nickname)
                .isMain(isMain)
                .build();
    }
    @Builder
    public CreateProfileRequest(String nickname, String phone, String address) {
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
    }
}
