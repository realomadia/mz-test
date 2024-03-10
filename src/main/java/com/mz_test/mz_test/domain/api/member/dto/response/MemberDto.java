package com.mz_test.mz_test.domain.api.member.dto.response;

import com.mz_test.mz_test.domain.api.member.entity.Member;
import com.mz_test.mz_test.domain.api.profile.dto.response.ProfileDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
public class MemberDto {
    private Long memberId;
    private String loginId;
    private String name;
    private String role;
    private List<ProfileDto> profileList;

    public MemberDto(Member member) {
        this.memberId = member.getMemberId();
        this.loginId = member.getLoginId();
        this.name = member.getName();
        this.role = member.getRole();
        this.profileList = member.getProfiles().stream().map(ProfileDto::new).collect(Collectors.toList());
    }


}
