package com.mz_test.mz_test.domain.api.profile.dto.response;

import com.mz_test.mz_test.domain.api.profile.entity.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileDto {

    private Long profileId;
    private String nickname;
    private String phone;
    private String address;


    public ProfileDto(Profile profile) {
        this.profileId = profile.getProfileId();
        this.nickname = profile.getNickname();
        this.phone = profile.getPhone();
        this.address = profile.getAddress();
    }
}
