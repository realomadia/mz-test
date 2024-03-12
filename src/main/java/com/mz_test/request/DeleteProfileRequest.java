package com.mz_test.request;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeleteProfileRequest {
    @NotNull(message = "수정될 프로필을 선택해 주십시오.")
    private Long profileId;

    @NotBlank(message = "회원을 입력해 주십시오.")
    private Long memberId;
}
