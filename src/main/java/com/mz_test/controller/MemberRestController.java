package com.mz_test.controller;

import com.mz_test.request.CreateMemberRequest;
import com.mz_test.response.MemberDetailResponse;
import com.mz_test.response.MemberResponse;
import com.mz_test.service.MemberService;
import com.mz_test.global.config.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberRestController {

    private final MemberService memberService;

    //회원 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> create(@Validated @RequestBody CreateMemberRequest request) {
        return new ApiResponse<>(memberService.create(request));
    }

    // 회원 삭제
    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long memberId) {
        memberService.delete(memberId);
    }

    // 회원 조회 (상세)
    @GetMapping("/{memberId}")
    public ApiResponse<MemberDetailResponse> find(@PathVariable Long memberId) {
        return new ApiResponse<>(memberService.findDetail(memberId));
    }

    // 회원 조회 (전체)
    @GetMapping
    public ApiResponse<Page<MemberResponse>> findAllMembers(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "") String searchName) {
        return new ApiResponse<>(memberService.findAllMembers(page, size, searchName));
    }
}
