package com.mz_test.mz_test.domain.api.profile.controller;

import com.mz_test.mz_test.domain.api.profile.dto.request.AddProfileDto;
import com.mz_test.mz_test.domain.api.profile.dto.request.DeleteProfileDto;
import com.mz_test.mz_test.domain.api.profile.dto.request.EditProfileDto;
import com.mz_test.mz_test.domain.api.profile.service.ProfileService;
import com.mz_test.mz_test.global.dto.response.DefaultResponseDto;
import com.mz_test.mz_test.global.annotation.LoginCheck;
import com.mz_test.mz_test.global.config.model.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileRestController {

    private final ProfileService profileService;

    @Operation(summary = "프로필 등록", description = "프로필을 등록할때 쓰는 API.")
    @PostMapping
    @LoginCheck
    public ResponseEntity<ApiResponse<DefaultResponseDto>> addProfile(@Validated @RequestBody AddProfileDto profileDto, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(new ApiResponse<>(profileService.addProfile(profileDto, token)));
    }

    @Operation(summary = "프로필 수정", description = "프로필을 수정할때 쓰는 API.")
    @PutMapping
    @LoginCheck
    public ResponseEntity<ApiResponse<DefaultResponseDto>> editProfile(@Validated @RequestBody EditProfileDto profileDto, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(new ApiResponse<>(profileService.editProfile(profileDto, token)));
    }

    @Operation(summary = "프로필 삭제", description = "프로필을 삭제할때 쓰는 API.")
    @DeleteMapping
    @LoginCheck
    public ResponseEntity<ApiResponse<DefaultResponseDto>> deleteProfile(@Validated @RequestBody DeleteProfileDto deleteProfileDto,  @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(new ApiResponse<>(profileService.deleteProfile(deleteProfileDto, token)));
    }



}
