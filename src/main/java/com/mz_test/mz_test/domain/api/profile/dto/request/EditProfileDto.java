package com.mz_test.mz_test.domain.api.profile.dto.request;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileDto {

    @NotNull(message = "수정될 프로필을 선택해 주십시오.")
    @ApiModelProperty(value = "수정될 프로필", required = true)
    private Long profileId;

    @NotBlank(message = "별명을 입력해 주십시오.")
    @ApiModelProperty(value = "수정할 프로필 닉네임", required = true)
    private String nickname;

    @NotBlank(message = "휴대폰 번호를 입력해 주십시오.")
    @ApiModelProperty(value = "수정할 프로필 휴대폰 번호", required = true)
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대폰 번호를 재확인 해주십시오.")
    private String phone;

    @ApiModelProperty(value = "수정할 프로필 주소")
    private String address;
}
