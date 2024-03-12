package com.mz_test.service;

import com.mz_test.entity.Member;
import com.mz_test.exception.LoginIdDuplicateException;
import com.mz_test.exception.MemberNotFoundException;
import com.mz_test.fixture.MemberFixture;
import com.mz_test.fixture.ProfileFixture;
import com.mz_test.repository.MemberRepository;
import com.mz_test.repository.ProfileRepository;
import com.mz_test.request.CreateMemberRequest;
import com.mz_test.request.CreateProfileRequest;
import com.mz_test.response.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@Nested
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberService memberService;

    @DisplayName("올바른 회원 정보가 주어지면 회원이 생성한다.")
    @Test
    void createTest() {
        // given
        CreateProfileRequest profileRequest = ProfileFixture.createProfileRequest();

        CreateMemberRequest memberRequest = MemberFixture.createMemberRequest(profileRequest);

        Member member = MemberFixture.idMember();

        given(memberRepository.existsByLoginId(any())).willReturn(false);
        given(memberRepository.save(any())).willReturn(member);

        // when
        Long id = memberService.create(memberRequest);
        // then
        assertThat(id).isEqualTo(member.getId());
    }
    @DisplayName("이미 있는 아이디로 가입 할려고하면 LoginIdDuplicateException 을 반환한다.")
    @Test
    void createExceptionTEST() {
        // given
        CreateProfileRequest profileRequest = ProfileFixture.createProfileRequest();
        CreateMemberRequest memberRequest = MemberFixture.createMemberRequest(profileRequest);
        given(memberRepository.existsByLoginId(any())).willReturn(true);
        // when & then
        assertThatThrownBy(() -> { memberService.create(memberRequest); }).isInstanceOf(LoginIdDuplicateException.class);
    }

    @DisplayName("올바른 memberId를 주면 회원을 삭제한다.")
    @Test
    void deleteTest() {
        // given
        Member member = MemberFixture.idMember();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        // when
        memberService.delete(member.getId());

        // then
        then(profileRepository).should().deleteByMember(member);
        then(memberRepository).should().deleteById(member.getId());
    }
    @DisplayName("올바르지 않은 memberId를 주면 MemberNotFoundException 를 반환한다.")
    @Test
    void deleteExceptionTest() {
        // given
        Member member = MemberFixture.idMember();
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> { memberService.delete(member.getId()); }).isInstanceOf(MemberNotFoundException.class);
    }

    @DisplayName("올바른 페이지 정보와 검색어를 주면 Page<MemberResponse> 를 반환한다.")
    @Test
    void findAllMembersTest() {
        // Given
        int page = 0;
        int size = 10;
        String searchKeyword = "hi";

        // Mocking repository response
        when(profileRepository.findByIsMain(eq(searchKeyword), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // When
        Page<MemberResponse> result = memberService.findAllMembers(page, size, searchKeyword);
        // Then
        assertEquals(0,result.getContent().size());
    }

    @DisplayName("올바른 memberId를 주면 회원 상세정보를 조회한다.")
    @Test
    void findMemberTest() {
        // given
        Long memberId = 1L;
        Member member = MemberFixture.idMember(memberId);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        Member result = memberService.find(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(member.getId());
        assertThat(result.getName()).isEqualTo(member.getName());
    }

    @DisplayName("올바르지않은 memberId 를 주면 MemberNotFoundException 를 반환한다.")
    @Test
    void findMemberExceptionTest() {
        // given
        Long nonExistingMemberId = 2L;

        given(memberRepository.findById(nonExistingMemberId)).willReturn(Optional.empty());

        // when, then
        assertThrows(MemberNotFoundException.class, () -> memberService.find(nonExistingMemberId));
        assertThatThrownBy(() -> { memberService.find(nonExistingMemberId); }).isInstanceOf(MemberNotFoundException.class);
    }

}