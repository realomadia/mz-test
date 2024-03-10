package com.mz_test.mz_test.global.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultResponseDto {

    @ApiModelProperty(value = "메시지")
    private String message;

    @ApiModelProperty(value = "성공유무")
    private Boolean success;

    @Builder
    public DefaultResponseDto(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }
}
