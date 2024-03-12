package com.mz_test.response;

import com.mz_test.entity.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileResponse {

    private Long profileId;
    private String nickname;
    private String phone;
    private String address;


    public ProfileResponse(Profile profile) {
        this.profileId = profile.getId();
        this.nickname = profile.getNickname();
        this.phone = profile.getPhone();
        this.address = profile.getAddress();
    }
}
