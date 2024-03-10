package com.mz_test.mz_test.domain.api.role.controller;

import com.mz_test.mz_test.domain.api.role.dto.request.ChangeRoleDto;
import com.mz_test.mz_test.domain.api.role.service.RoleService;
import com.mz_test.mz_test.global.annotation.AdminCheck;
import com.mz_test.mz_test.global.config.model.ApiResponse;
import com.mz_test.mz_test.global.dto.response.DefaultResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
public class RoleRestController {

    private final RoleService roleService;

    @Operation(summary = "역할 변경", description = "회원의 역할을 관리자로 바꿀때 쓰는 API.")
    @AdminCheck
    @PutMapping("/admin")
    public ResponseEntity<ApiResponse<DefaultResponseDto>> promoteToAdmin(@Validated @RequestBody ChangeRoleDto changeRoleDto) {
        return ResponseEntity.ok(new ApiResponse<>(roleService.promoteToAdmin(changeRoleDto)));
    }

    @Operation(summary = "역할 변경", description = "회원의 역할을 회원으로 바꿀때 쓰는 API.")
    @AdminCheck
    @PutMapping("/member")
    public ResponseEntity<ApiResponse<DefaultResponseDto>> demoteToMember(@Validated @RequestBody ChangeRoleDto changeRoleDto) {
        return ResponseEntity.ok(new ApiResponse<>(roleService.demoteToMember(changeRoleDto)));
    }
}
