package com.mz_test.mz_test.global.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;

public class BcryptUtil {

    private static final int SALT_LENGTH = 16;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 패스워드와 솔트를 이용한 암호화 메서드
    public static String hashPasswordWithSalt(String password, String salt) {
        // 솔트와 패스워드를 합쳐서 인코딩
        String saltedPassword = password + salt;
        return passwordEncoder.encode(saltedPassword);
    }

    // 솔트 생성 메서드
    public static String generateSalt() {
        // 랜덤한 솔트 생성
        byte[] saltBytes = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    // 패스워드 검증 메서드
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
