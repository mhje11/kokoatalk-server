package org.kokoatalkserver.global.util.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenizer {
    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    public static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; //30분
    public static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; //7일

    public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecret, @Value("${jwt.refreshKey}") String refreshSecret) {
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Jwts 빌더를 이용한 token 생성
     */
    private String createToken(Long id, String userId, String role, long expire, byte[] secretKey) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("id", id);
        claims.put("role", role);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expire);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, getSigningKey(secretKey))
                .compact();
    }

    /**
     * byte 형식의 secretKey
     */
    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }

    /**
     * Accesstoken 생성
     */
    public String createAccessToken(Long id, String username, String role) {
        log.debug("Creating access token for user: {}", username);
        log.debug("Using secret key: {}", new String(accessSecret).substring(0, 10) + "...");
        return createToken(id, username, role, ACCESS_TOKEN_EXPIRE_COUNT, accessSecret);
    }


    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(Long id, String username, String role) {
        return createToken(id, username, role, REFRESH_TOKEN_EXPIRE_COUNT, refreshSecret);
    }


    /**
     * access token 파싱
     */
    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    /**
     * refresh token 파싱
     */
    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    /**
     * token 파싱
     *
     * @param token     access/refresh
     * @param secretKey token 값
     * @return
     */
    private Claims parseToken(String token, byte[] secretKey) {
        log.debug("Parsing token: {}", token.substring(0, Math.min(token.length(), 10)));
        log.debug("Using secret key: {}", new String(secretKey, 0, Math.min(secretKey.length, 10)));

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    /**
     * 토큰 만료 확인
     */
    public boolean isTokenExpired(String token, byte[] secretKey) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey(secretKey)).build().parseClaimsJws(token).getBody();
            return false;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        }
    }

    public boolean isAccessTokenExpired(String accessToken) {
        return isTokenExpired(accessToken, accessSecret);
    }

    public boolean isRefreshTokenExpired(String refreshToken) {
        return isTokenExpired(refreshToken, refreshSecret);
    }

    /**
     * accessToken 만료 시 refreshToken 으로 재발급
     */
    public String newAccessToken(String refreshToken) {
        Claims claims = parseRefreshToken(refreshToken);

        Long id = claims.get("id", Long.class);
        String userId = claims.getSubject();
        String name = claims.get("name", String.class);
        String role = claims.get("role", String.class);

        return createAccessToken(id, userId, role);
    }

    /**
     * RefreshToken에서 사용자 ID 추출
     */
    public String extractUserIdFromRefreshToken(String refreshToken) {
        try {
            Claims claims = parseRefreshToken(refreshToken);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting user ID from refresh token: {}", e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }
    }

    public boolean validateToken(String token, byte[] secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(secretKey))
                    .build()
                    .parseClaimsJws(token);
            return true; // 유효한 토큰
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            return false; // 만료된 토큰
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("Invalid token: {}", e.getMessage());
            return false; // 잘못된 토큰
        }
    }

    /**
     * AccessToken 유효성 검사
     */
    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, accessSecret);
    }

    /**
     * RefreshToken 유효성 검사
     */
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, refreshSecret);
    }

}