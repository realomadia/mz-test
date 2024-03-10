package com.mz_test.mz_test.global.aspect;

import com.mz_test.mz_test.global.config.exception.CustomException;
import com.mz_test.mz_test.global.config.exception.ErrorCode;
import com.mz_test.mz_test.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class TokenValidationAspect {

    private final JwtUtil jwtUtil;

    @Pointcut("@annotation(com.mz_test.mz_test.global.annotation.LoginCheck)")
    public void loginCheckPointcut() {
    }

    @Pointcut("@annotation(com.mz_test.mz_test.global.annotation.AdminCheck)")
    public void adminCheck() {
    }

    @Before("loginCheckPointcut()")
    public void beforeMethodWithLoginCheck(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty() || !jwtUtil.validateToken(token)) {
            throw new CustomException("INVALID_TOKEN OR TOKEN_DOES_NOT_EXIST", ErrorCode.LOGIN_REQUIRED);
        }
    }


    @Before("adminCheck()")
    public void beforeMethodWithAdminCheck(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        if (!jwtUtil.isAdmin(token)) {
            throw new CustomException("UNAUTHORIZED_MEMBER_ACCESS", ErrorCode.HANDLE_ACCESS_DENIED);
        }
    }
}
