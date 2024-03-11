package com.mz_test.mz_test.domain.api.profile.service;

import com.mz_test.mz_test.domain.api.member.entity.Member;
import com.mz_test.mz_test.domain.api.member.repository.MemberRepository;
import com.mz_test.mz_test.domain.api.profile.dto.request.AddProfileDto;
import com.mz_test.mz_test.domain.api.profile.dto.request.DeleteProfileDto;
import com.mz_test.mz_test.domain.api.profile.dto.request.EditProfileDto;
import com.mz_test.mz_test.domain.api.profile.entity.Profile;
import com.mz_test.mz_test.domain.api.profile.repository.ProfileRepository;
import com.mz_test.mz_test.global.dto.response.DefaultResponseDto;
import com.mz_test.mz_test.global.config.exception.CustomException;
import com.mz_test.mz_test.global.config.exception.ErrorCode;
import com.mz_test.mz_test.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;

    private final JwtUtil jwtUtil;


    @Transactional
    public DefaultResponseDto addProfile(AddProfileDto addProfileDto, String token) {
        String id = jwtUtil.getMemberIdFromToken(token);
        Optional<Member> optionalMember = memberRepository.findByLoginId(id);
        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.addProfile(addProfileDto);
            return DefaultResponseDto.builder().message("프로필 추가 성공").success(true).build();
        } else {
            throw new CustomException("MEMBER_NOT_FOUND", ErrorCode.INVALID_TOKEN);
        }
    }

    @Transactional
    public DefaultResponseDto editProfile(EditProfileDto editProfileDto, String token) {
        String id = jwtUtil.getMemberIdFromToken(token);
        Optional<Member> optionalMember = memberRepository.findByLoginId(id);
        if(optionalMember.isPresent()){
            Member member = optionalMember.get();
            Optional<Profile> optionalProfile = profileRepository.findByProfileIdAndMember(editProfileDto.getProfileId(), member);
            if(optionalProfile.isPresent()){
                Profile profile = optionalProfile.get();
                profile.editProfile(editProfileDto);
            }else{
                throw new CustomException("PROFILE_NOT_FOUND", ErrorCode.INVALID_INPUT_VALUE);
            }
        } else {
            throw new CustomException("MEMBER_NOT_FOUND", ErrorCode.INVALID_TOKEN);
        }
        return DefaultResponseDto.builder().success(true).message("프로필 수정 성공").build();
    }

    @Transactional
    public DefaultResponseDto deleteProfile(DeleteProfileDto deleteProfileDto, String token) {
        String id = jwtUtil.getMemberIdFromToken(token);
        Optional<Member> optionalMember = memberRepository.findByLoginId(id);
        if(optionalMember.isPresent()){
            Member member = optionalMember.get();
            member.deleteProfile(deleteProfileDto);
            profileRepository.deleteById(deleteProfileDto.getProfileId());
        }else{
            throw new CustomException("MEMBER_NOT_FOUND", ErrorCode.INVALID_TOKEN);
        }

        return DefaultResponseDto.builder().success(true).message("프로필 삭제 완료").build();
    }
}




