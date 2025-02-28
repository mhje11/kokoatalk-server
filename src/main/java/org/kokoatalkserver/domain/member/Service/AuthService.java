package org.kokoatalkserver.domain.member.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.member.dto.SignUpRequestDto;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.kokoatalkserver.global.util.jwt.entity.RefreshToken;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        if(memberRepository.findByLoginId(signUpRequestDto.getAccountId()).isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_USER_ID);
        }

        Member member = Member.builder()
                .loginId(signUpRequestDto.getAccountId())
                .nickname(signUpRequestDto.getNickname())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public String[] login(String userId, String password, Boolean rememberMe) {
        Member member = memberRepository.findByLoginId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ExceptionCode.PASSWORD_MISMATCH);
        }
        member.rememberMe(rememberMe);
        memberRepository.save(member);

        String accessToken = jwtTokenizer.createAccessToken(member.getKokoaId(), member.getLoginId(), member.getRole().name());

        // 리프레시 토큰 검증
        RefreshToken existingToken = refreshTokenService.findRefreshTokenByLoginId(member.getLoginId())
                .orElse(null);

        String refreshToken;
        Long refreshTokenTTL = JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000;
        if (existingToken == null || existingToken.isExpired()) {
            refreshToken = jwtTokenizer.createRefreshToken(member.getKokoaId(), member.getLoginId(), member.getRole().name());
            RefreshToken newRefreshToken = new RefreshToken(member.getLoginId(), refreshToken, refreshTokenTTL);
            refreshTokenService.saveRefreshToken(newRefreshToken);
        } else {
            refreshToken = existingToken.getRefresh();
        }

        return new String[]{accessToken, refreshToken};
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(ExceptionCode.UNAUTHORIZED);
        }
        refreshTokenService.deleteAllRefreshTokenData(refreshToken);
    }

}
