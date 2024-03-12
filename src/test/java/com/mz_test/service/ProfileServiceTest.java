package com.mz_test.service;

import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import com.mz_test.exception.MemberNotFoundException;
import com.mz_test.exception.ProfileNotFoundException;
import com.mz_test.exception.ProfileRemovalNotAllowedException;
import com.mz_test.fixture.MemberFixture;
import com.mz_test.fixture.ProfileFixture;
import com.mz_test.repository.MemberRepository;
import com.mz_test.repository.ProfileRepository;
import com.mz_test.request.EditProfileRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@Nested
@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ProfileService profileService;

    @DisplayName("존재하는 profileId를 조회하면 프로필 정보를 반환한다.")
    @Test
    void findTest() {
        // given
        final Long profileId = 1L;

        final Profile profile = ProfileFixture.profile(profileId);

        given(profileRepository.findById(profileId)).willReturn(Optional.of(profile));

        // when
        profileService.find(profileId);

        // then
        then(profileRepository).should().findById(profileId);
    }


    @DisplayName("존재하지 않는 profileId를 조회하면 ProfileNotFoundException를 반환한다.")
    @Test
    void findExceptionTest() {
        // when & then
        assertThatThrownBy(() -> { profileService.find(-1L); }).isInstanceOf(ProfileNotFoundException.class);
    }

    @Test
    @DisplayName("존재하는 memberId를 조회하면 회원정보를 반환한다")
    void findMemberByIdTest() {
        // given
        final Long memberId = 1L;
        final Member expectedMember = MemberFixture.idMember();

        given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(expectedMember));

        // when
        Member result = profileService.findMemberById(memberId);

        // then
        then(memberRepository).should().findById(memberId);

        // 반환된 Member 객체가 예상된 객체와 같은지 확인
        assertThat(result).isEqualTo(expectedMember);
    }

    @DisplayName("존재하지 않는 memberId를 조회하면 MemberNotFoundException 를 반환한다.")
    @Test
    void findMemberByIdExceptionTest() {
        // when & then
        assertThatThrownBy(() -> { profileService.findMemberById(-1L); }).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("올바른 memberId와 profileId가 주어지면 예외가 발생하지 않아야 한다.")
    void validMemberProfileTest() {
        // given
        final Long memberId = 1L;
        final Long profileId = 1L;

        final Member member = MemberFixture.idMember();
        final Profile profile = ProfileFixture.profile(profileId);

        given(profileRepository.findById(eq(profileId))).willReturn(Optional.of(profile));

        // when & then
        assertThatCode(() -> profileService.validMemberProfile(memberId, profileId)).doesNotThrowAnyException();
        then(profileRepository).should().findById(profileId);
    }

    @Test
    @DisplayName("올바른 memberId와 profileId, request가 주어지면 예외가 발생하지 않아야 한다.")
    void validMemberProfileExceptionTest() {
        // given
        final Long memberId = 1L;
        final Long profileId = 1L;

        final Profile profile = ProfileFixture.profile(profileId);

        given(profileRepository.findById(eq(profileId))).willReturn(Optional.of(profile));

        EditProfileRequest editProfileRequest = EditProfileRequest.builder().phone("010-4477-5644").address("부산시").nickname("identified").build();
        // when & then
        assertThatCode(() ->
                profileService.editProfile(memberId, profileId, editProfileRequest))
                .doesNotThrowAnyException();

        then(profileRepository).should(times(2)).findById(profileId);

    }
    @Test
    @DisplayName("프로필이 두개 이상 남아 있을 때 프로필 삭제한다.")
    void deleteTest() {

        // given
        final Long memberId = 1L;
        final Long profileIdToDelete = 1L;
        final Long otherProfileId = 2L;

        final Member member = MemberFixture.idMember();

        final Profile profileToDelete = ProfileFixture.profile(profileIdToDelete);
        final Profile otherProfile = ProfileFixture.profile(otherProfileId);

        given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(member));

        given(profileRepository.findAllByMember(eq(member))).willReturn(List.of(profileToDelete, otherProfile));

        given(profileRepository.findById(eq(profileIdToDelete))).willReturn(Optional.of(profileToDelete));

        // when
        profileService.delete(memberId, profileIdToDelete);

        // then
        then(profileRepository).should().deleteById(profileIdToDelete);
    }


    @Test
    @DisplayName("다른 프로필이 존재하지 않을때 ProfileRemovalNotAllowedException 을 반환한다.")
    void deleteExceptionTest() {

        // given
        final Long memberId = 1L;
        final Long profileIdToDelete = 1L;

        final Member member = MemberFixture.idMember();

        final Profile profileToDelete = ProfileFixture.profile(profileIdToDelete);

        given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(member));

        given(profileRepository.findAllByMember(eq(member))).willReturn(List.of(profileToDelete));

        given(profileRepository.findById(eq(profileIdToDelete))).willReturn(Optional.of(profileToDelete));

        // when & then
        assertThatThrownBy(() -> {  profileService.delete(memberId, profileIdToDelete); }).isInstanceOf(ProfileRemovalNotAllowedException.class);

    }
}