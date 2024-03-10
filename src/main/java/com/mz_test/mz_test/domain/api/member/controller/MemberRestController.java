package com.mz_test.mz_test.domain.api.member.controller;

import com.mz_test.mz_test.domain.api.member.dto.request.AddMemberDto;
import com.mz_test.mz_test.domain.api.member.dto.request.LoginDto;
import com.mz_test.mz_test.domain.api.member.dto.response.MemberDto;
import com.mz_test.mz_test.domain.api.member.dto.response.MembersDto;
import com.mz_test.mz_test.domain.api.member.service.MemberService;
import com.mz_test.mz_test.global.dto.response.DefaultResponseDto;
import com.mz_test.mz_test.global.annotation.AdminCheck;
import com.mz_test.mz_test.global.config.model.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberRestController {

    private final MemberService memberService;

    @Operation(summary = "회원 등록", description = "회원을 등록할때 쓰는 API.")
    @PostMapping
    public ResponseEntity<ApiResponse<DefaultResponseDto>> addUser(@Validated @RequestBody AddMemberDto memberDto) {
        return ResponseEntity.ok(new ApiResponse<>(memberService.addMember(memberDto)));
    }
    @Operation(summary = "회원 로그인", description = "로그인시 쓰는 API")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Validated @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(new ApiResponse<>(memberService.login(loginDto)));
    }
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴할때 쓰는 API")
    @DeleteMapping
    public ResponseEntity<ApiResponse<DefaultResponseDto>> deleteMember(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(new ApiResponse<>(memberService.deleteMember(token)));
    }
    @Operation(summary = "회원 전체 조회", description = "회원을 전체 조회 할때 쓰는 API")
    @AdminCheck
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<MembersDto>>> getAllMembers(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "") String searchName) {
        return ResponseEntity.ok(new ApiResponse<>(memberService.getAllMembers(page, size, searchName)));
    }

    @Operation(summary = "회원 조회", description = "특정 회원을 조회 할때 쓰는 API")
    @AdminCheck
    @GetMapping
    public ResponseEntity<ApiResponse<MemberDto>> getMember(@RequestParam Long memberId) {
        return ResponseEntity.ok(new ApiResponse<>(memberService.getMember(memberId)));
    }


}
