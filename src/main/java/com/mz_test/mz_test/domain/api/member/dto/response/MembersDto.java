package com.mz_test.mz_test.domain.api.member.dto.response;

import com.mz_test.mz_test.domain.api.member.entity.Member;
import com.mz_test.mz_test.domain.api.profile.dto.response.ProfileDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@ToString
@Getter
@Setter
@NoArgsConstructor
public class MembersDto {

    private Long memberId;
    private String loginId;
    private String name;
    private String role;
    private ProfileDto profileDto;

    public MembersDto(Member member) {
        this.memberId = member.getMemberId();
        this.loginId = member.getLoginId();
        this.name = member.getName();
        this.role = member.getRole();

        this.profileDto = member.getProfiles().stream()
                .filter(profile -> profile.getIsMain() == 1)
                .findFirst()
                .map(ProfileDto::new)
                .orElse(null);
    }
}
