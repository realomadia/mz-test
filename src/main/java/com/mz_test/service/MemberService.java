package com.mz_test.service;

import com.mz_test.entity.Member;
import com.mz_test.entity.Profile;
import com.mz_test.exception.LoginIdDuplicateException;
import com.mz_test.exception.MemberNotFoundException;
import com.mz_test.global.util.BcryptUtil;
import com.mz_test.repository.MemberRepository;
import com.mz_test.repository.ProfileRepository;
import com.mz_test.request.CreateMemberRequest;
import com.mz_test.response.MemberDetailResponse;
import com.mz_test.response.MemberResponse;
import com.mz_test.response.PasswordResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;


    @Transactional
    public Long create(final CreateMemberRequest request) {

        PasswordResponse passwordResponse = encryption(request.getPassword());

        boolean isEmpty = memberRepository.existsByLoginId(request.getLoginId());
        if (!isEmpty) {
            Member member = memberRepository.save(request.toEntity(passwordResponse));

            profileRepository.save(request.getCreateProfileRequest().toEntity(member, 1));
            return member.getId();
        } else {
            throw new LoginIdDuplicateException(request.getLoginId());
        }
    }

    @Transactional
    public void delete(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        profileRepository.deleteByMember(member);
        memberRepository.deleteById(memberId);
    }

    public Page<MemberResponse> findAllMembers(int page, int size, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, size);
        return profileRepository.findByIsMain(searchKeyword, pageable).map(MemberResponse::new);
    }

    public Member find(@NotNull final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    public MemberDetailResponse findDetail(Long memberId) {
        Member member = find(memberId);
        List<Profile> profiles = profileRepository.findAllByMember(member);
        return new MemberDetailResponse(member, profiles);
    }

    public PasswordResponse encryption(String password) {
        String salt = BcryptUtil.generateSalt();
        String saltedPassword = BcryptUtil.hashPasswordWithSalt(password, salt);

        return PasswordResponse.builder().salt(salt).password(saltedPassword).build();
    }


}
