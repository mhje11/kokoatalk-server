package org.kokoatalkserver.global.util.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.kokoatalkserver.global.util.jwt.entity.RefreshToken;
import org.kokoatalkserver.global.util.jwt.repository.RefreshTokenRepository;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenizer jwtTokenizer;
    private final RedisTemplate<String, RefreshToken> redisTemplate;


    //RefreshToken 저장
    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }


    //DB에서 RefreshToken 값 조회
    public Optional<RefreshToken> findByRefresh(String refresh) {
        return refreshTokenRepository.findByRefresh(refresh);
    }


    public boolean isRefreshTokenValid(String refreshToken) {
        return findByRefresh(refreshToken).isPresent() && !jwtTokenizer.isRefreshTokenExpired(refreshToken);
    }

    //사용자ID로 RefreshToken 조회
    public Optional<RefreshToken> findByKokoaId(String userId) {
        return refreshTokenRepository.findByKokoaId(userId);
    }

    @Transactional
    public void deleteRefreshToken(String refresh) {
        String key = "refresh_token:refresh:" + refresh;
        Boolean deleted = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("리프레시 토큰이 정상적으로 삭제 됐습니다 : {}", key);
        } else {
            log.warn("리프레시 토큰 삭제 실패 : {}", key);
        }
    }

    @Transactional
    public void deleteAllRefreshTokenData(String refresh) {
        // refresh_token:refresh:<token> 삭제
        String refreshKey = "refresh_token:refresh:" + refresh;
        Boolean refreshDeleted = redisTemplate.delete(refreshKey);
        if (Boolean.TRUE.equals(refreshDeleted)) {
            log.info("리프레시 토큰 키 삭제 성공: {}", refreshKey);
        } else {
            log.warn("리프레시 토큰 키 삭제 실패: {}", refreshKey);
        }

        // refresh_token:<username>:idx 삭제
        String userIdxKey = "refresh_token:" + extractUserIdFromRefreshToken(refresh) + ":idx";
        Boolean userIdxDeleted = redisTemplate.delete(userIdxKey);
        if (Boolean.TRUE.equals(userIdxDeleted)) {
            log.info("리프레시 토큰 인덱스 삭제 성공: {}", userIdxKey);
        } else {
            log.warn("리프레시 토큰 인덱스 삭제 실패: {}", userIdxKey);
        }

        // refresh_token:<username> 해시 테이블 삭제
        String hashKey = "refresh_token:" + extractUserIdFromRefreshToken(refresh);
        Boolean hashDeleted = redisTemplate.delete(hashKey);
        if (Boolean.TRUE.equals(hashDeleted)) {
            log.info("리프레시 토큰 해시 삭제 성공: {}", hashKey);
        } else {
            log.warn("리프레시 토큰 해시 삭제 실패: {}", hashKey);
        }
    }

    public String getUserIdFromRefreshToken(String refreshToken) {
        Optional<RefreshToken> optionalToken = findByRefresh(refreshToken);
        if (optionalToken.isEmpty()) {
            throw new CustomException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND);
        }
        return optionalToken.get().getKokoaId(); // 사용자 ID 반환
    }

    private String extractUserIdFromRefreshToken(String refreshToken) {
        // RefreshToken에서 사용자 ID 추출 로직 구현
        // 예: JWT를 디코드하여 사용자 ID를 가져오는 방식
        return jwtTokenizer.extractUserIdFromRefreshToken(refreshToken);
    }
}