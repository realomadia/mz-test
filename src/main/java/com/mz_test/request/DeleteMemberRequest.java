package com.mz_test.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteMemberRequest {
    @NotBlank(message = "회원을 입력해 주십시오.")
    private Long memberId;
}
