package org.kokoatalkserver.global.util.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.kokoatalkserver.global.util.jwt.entity.RefreshToken;
import org.kokoatalkserver.global.util.jwt.repository.RefreshTokenRepository;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenizer jwtTokenizer;


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
        int deletedCount = refreshTokenRepository.deleteByRefresh(refresh);
        if (deletedCount == 0) {
            log.warn("Refresh token not found or already deleted: {}", refresh);
            throw new CustomException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND);
        }
        log.info("Refresh token deleted successfully: {}", refresh);
    }


}