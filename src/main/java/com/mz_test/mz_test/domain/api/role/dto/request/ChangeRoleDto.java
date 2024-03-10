package com.mz_test.mz_test.domain.api.role.dto.request;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeRoleDto {
    @NotNull(message = "수정될 프로필을 선택해 주십시오.")
    @ApiModelProperty(value = "수정될 프로필", required = true)
    private Long memberId;
}
