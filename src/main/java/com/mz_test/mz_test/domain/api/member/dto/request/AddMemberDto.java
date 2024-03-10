package com.mz_test.mz_test.domain.api.member.dto.request;

import com.mz_test.mz_test.domain.api.profile.dto.request.AddProfileDto;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AddMemberDto {

    @ApiModelProperty(value = "사용자 이름", required = true)
    @NotBlank(message = "이름을 입력해 주세요.")
    private String name;

    @ApiModelProperty(value = "사용자 로그인 아이디", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디에는 공백이 포함될 수 없습니다.")
    @Size(min = 5,max = 18, message = "아이디는 5자 이상 18자 이하로 입력해 주십시오.")
    @NotBlank(message = "아이디를 입력해 주세요.")
    private String loginId;

    @ApiModelProperty(value = "사용자 패스워드", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "패스워드에는 공백이 포함될 수 없습니다.")
    @Size(min = 8, message = "패스워드는 8자 이상 입력해 주십시오.")
    @NotBlank(message = "패스워드를 입력해 주세요.")
    private String password;

    @ApiModelProperty(value = "사용자 솔트")
    private String salt;

    @Valid
    @ApiModelProperty(value = "사용자 프로필", required = true)
    private AddProfileDto addProfileDto;
}
