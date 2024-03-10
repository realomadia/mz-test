package com.mz_test.mz_test.global.util;

import com.mz_test.mz_test.global.config.exception.CustomException;
import com.mz_test.mz_test.global.config.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTimeMillis;

    private static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);


    /**
     * 사용자 ID를 이용하여 JWT 토큰을 생성하는 메서드
     *
     * @param id 사용자 ID
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String id, String role) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTimeMillis);

        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .claim("role", role)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 ID를 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return 추출된 사용자 ID
     * @throws SignatureException JWT 토큰 파싱에 실패한 경우 발생하는 예외
     */
    public String getMemberIdFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new SignatureException("JWT 토큰을 구문 분석하는 데 실패했습니다", e);
        }
    }


    /**
     * JWT 토큰의 유효성을 검증하는 메서드
     *
     * @param token JWT 토큰
     * @return 유효한 토큰인 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)  // Use the same key used for signing
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 예외가 발생하면 토큰이 유효하지 않음
            return false;
        }
    }

    /**
     * 받은 토큰의 role이 admin인지 아닌지를 체크하는 메서드
     *
     * @param token JWT 토큰
     * @return 토큰의 role이 "admin"인 경우 true, 아닌 경우 false
     * @throws SignatureException JWT 토큰 파싱에 실패한 경우 발생하는 예외
     */
    public boolean isAdmin(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = (String) claims.get("role");
            return "admin".equals(role);
        } catch (Exception e) {
            // 예외 발생시 토큰이 유효하지 않음
            return false;
        }
    }

}
