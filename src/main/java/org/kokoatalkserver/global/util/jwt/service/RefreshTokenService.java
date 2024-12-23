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

    public String getUserIdFromRefreshToken(String refreshToken) {
        Optional<RefreshToken> optionalToken = findByRefresh(refreshToken);
        if (optionalToken.isEmpty()) {
            throw new CustomException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND);
        }
        return optionalToken.get().getKokoaId(); // 사용자 ID 반환
    }
}