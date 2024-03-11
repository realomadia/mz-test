package com.mz_test.mz_test.domain.api.member.service;


import com.mz_test.mz_test.domain.api.member.dto.request.AddMemberDto;
import com.mz_test.mz_test.domain.api.member.dto.request.LoginDto;
import com.mz_test.mz_test.domain.api.member.dto.response.MemberDto;
import com.mz_test.mz_test.domain.api.member.dto.response.MembersDto;
import com.mz_test.mz_test.domain.api.member.entity.Member;
import com.mz_test.mz_test.domain.api.member.repository.MemberRepository;
import com.mz_test.mz_test.global.dto.response.DefaultResponseDto;
import com.mz_test.mz_test.global.config.exception.CustomException;
import com.mz_test.mz_test.global.config.exception.ErrorCode;
import com.mz_test.mz_test.global.util.BcryptUtil;
import com.mz_test.mz_test.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    private final JwtUtil jwtUtil;

    @Transactional
    public DefaultResponseDto addMember(AddMemberDto memberDto) {
        // 비밀번호 암호화
        String salt = BcryptUtil.generateSalt();
        String saltedPassword = BcryptUtil.hashPasswordWithSalt(memberDto.getPassword(), salt);
        memberDto.setSalt(salt);
        memberDto.setPassword(saltedPassword);
        // Member 조회 (이미 저장된 경우 더티 체킹을 위해)
        Optional<Member> optionalMember = memberRepository.findByLoginId(memberDto.getLoginId());

        if (optionalMember.isEmpty()) {
            // 새로운 Member 생성
            Member member = Member.createWithProfile(memberDto);
            memberRepository.save(member);
            return DefaultResponseDto.builder().success(true).message("회원가입 완료").build();
        }
        throw new CustomException("w",ErrorCode.SIGN_UP_DUPLICATE);
    }
    public String login(LoginDto loginDto) {
        try {
            Optional<Member> optionalMember = memberRepository.findByLoginId(loginDto.getId());
            if (optionalMember.isPresent()) {
                Member member = optionalMember.get();
                boolean passwordMatches = BcryptUtil.verifyPassword(loginDto.getPassword() + member.getSalt(), member.getPassword());
                if (passwordMatches) {
                    return jwtUtil.generateToken(loginDto.getId(), member.getRole());
                }
            }
        } catch (Exception e) {
            throw new CustomException("UNKNOWN_ERROR", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        throw new CustomException("LOGIN_FAILED", ErrorCode.SIGN_IN_FAILED);
    }

    @Transactional
    public DefaultResponseDto deleteMember(String token) {
        String memberId = jwtUtil.getMemberIdFromToken(token);
        memberRepository.deleteByLoginId(memberId);
        return DefaultResponseDto.builder().message("회원 삭제 완료").success(true).build();
    }

    public Page<MembersDto> getAllMembers(int page, int size, String searchName) {
        Pageable pageable = PageRequest.of(page, size);
        return memberRepository.findByNameContainingIgnoreCase(searchName, pageable).map(MembersDto::new);
    }

    public MemberDto getMember(Long memberId) {
        MemberDto memberDto;
        if(memberId == null){
            throw new CustomException("INVALID_INPUT_VALUE",ErrorCode.INVALID_INPUT_VALUE);
        }
        Optional<Member> optionalMember = memberRepository.findById(memberId);
            if(optionalMember.isPresent()){
                memberDto = new MemberDto(optionalMember.get());
            }else{
                throw new CustomException("MEMBER_NOT_FOUND", ErrorCode.MEMBER_NOT_FOUND);
        }
        return memberDto;
    }

}
