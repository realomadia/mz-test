package com.mz_test.mz_test.domain.api.role.service;


import com.mz_test.mz_test.domain.api.member.entity.Member;
import com.mz_test.mz_test.domain.api.member.repository.MemberRepository;
import com.mz_test.mz_test.domain.api.role.dto.request.ChangeRoleDto;
import com.mz_test.mz_test.global.dto.response.DefaultResponseDto;
import com.mz_test.mz_test.global.config.exception.CustomException;
import com.mz_test.mz_test.global.config.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoleService {

    private final MemberRepository memberRepository;
    @Transactional
    public DefaultResponseDto promoteToAdmin(ChangeRoleDto changeRoleDto) {
        Member member = memberRepository.findById(changeRoleDto.getMemberId()).orElseThrow(() -> new CustomException("Member_NOT_FOUND ID: " + changeRoleDto.getMemberId(), ErrorCode.MEMBER_NOT_FOUND));
        member.promoteToAdmin();
        memberRepository.save(member);
        return DefaultResponseDto.builder().message(member.getLoginId() + "님이 관리자가 되었습니다.").success(true).build();
    }
    @Transactional
    public DefaultResponseDto demoteToMember(ChangeRoleDto changeRoleDto) {
        Member member = memberRepository.findById(changeRoleDto.getMemberId()).orElseThrow(() -> new CustomException("MEMBER_NOT_FOUND ID: " + changeRoleDto.getMemberId(), ErrorCode.MEMBER_NOT_FOUND));
        member.demoteToMember();
        memberRepository.save(member);
        return DefaultResponseDto.builder().message(member.getLoginId() + "님이 관리자가 취소 되었습니다.").success(true).build();
    }
}
