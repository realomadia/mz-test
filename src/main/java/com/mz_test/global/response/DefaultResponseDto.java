package com.mz_test.global.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultResponseDto {

    private String message;

    private Boolean success;

    @Builder
    public DefaultResponseDto(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }
}
