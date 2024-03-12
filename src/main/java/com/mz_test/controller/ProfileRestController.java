package com.mz_test.controller;

import com.mz_test.request.CreateProfileRequest;
import com.mz_test.request.EditProfileRequest;
import com.mz_test.service.ProfileService;
import com.mz_test.global.config.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/")
public class ProfileRestController {

    private final ProfileService profileService;

    // 회원 프로필 추가
    @PostMapping("/{memberId}/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> addProfile(@PathVariable Long memberId,
                                        @Validated @RequestBody CreateProfileRequest request) {
        return new ApiResponse<>(profileService.addProfile(memberId, request));
    }

    // 회원 프로필 수정
    @PutMapping("/{memberId}/profiles/{profileId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void editProfile(@PathVariable Long memberId,
                            @PathVariable Long profileId,
                            @Validated @RequestBody EditProfileRequest request) {
        profileService.editProfile(memberId, profileId, request);
    }

    //회원 프로필 삭제
    @DeleteMapping("/{memberId}/profiles/{profileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@PathVariable Long memberId,
                              @PathVariable Long profileId) {
        profileService.delete(memberId, profileId);
    }
}
