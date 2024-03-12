package com.mz_test.service;

import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import com.mz_test.exception.IdNotMatchException;
import com.mz_test.exception.MemberNotFoundException;
import com.mz_test.exception.ProfileNotFoundException;
import com.mz_test.exception.ProfileRemovalNotAllowedException;
import com.mz_test.repository.MemberRepository;
import com.mz_test.repository.ProfileRepository;
import com.mz_test.request.CreateProfileRequest;
import com.mz_test.request.EditProfileRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {


    private final MemberRepository memberRepository;

    private final ProfileRepository profileRepository;

    public Profile find(@NotNull final Long profileId) {
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException(profileId));
        return profile;
    }

    public Member findMemberById(@NotNull final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    @Transactional
    public Long addProfile(Long memberId, CreateProfileRequest request) {
        Member member = findMemberById(memberId);
        return profileRepository.save(request.toEntity(member, 0)).getId();
    }

    public void validMemberProfile(Long memberId, Long profileId) {
        Profile profile = find(profileId);
        if (!Objects.equals(profile.getMember().getId(), memberId)) {
            throw new IdNotMatchException();
        }
    }

    @Transactional
    public void editProfile(Long memberId, Long profileId, EditProfileRequest request) {
        validMemberProfile(memberId, profileId);

        Profile profile = find(profileId);
        profile.modify(request.getNickname(), request.getPhone(), request.getAddress());
    }

    @Transactional
    public void delete(Long memberId, Long profileId) {
        validMemberProfile(memberId, profileId);
        Member member = findMemberById(memberId);
        List<Profile> profiles = profileRepository.findAllByMember(member);
        if (profiles.size() < 2) {
            throw new ProfileRemovalNotAllowedException();
        }

        Profile profileToDelete = profiles.stream()
                .filter(profile -> profile.getId().equals(profileId))
                .findAny()
                .orElseThrow(() -> new ProfileNotFoundException(profileId));
        if (profileToDelete.getIsMain() == 1) {
            Profile nextProfile = profiles.stream()
                    .filter(profile -> !profile.getId().equals(profileId))
                    .findAny()
                    .orElse(null);
            if (nextProfile != null) {
                nextProfile.changeMain();
            }
        }
        profileRepository.deleteById(profileId);
    }
}




