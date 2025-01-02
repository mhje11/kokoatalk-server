package org.kokoatalkserver.domain.member.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.member.Service.AuthService;
import org.kokoatalkserver.domain.member.Service.MemberService;
import org.kokoatalkserver.domain.member.dto.LoginRequestDto;
import org.kokoatalkserver.domain.member.dto.LoginResponseDto;
import org.kokoatalkserver.domain.member.dto.MemberLoginResponseDto;
import org.kokoatalkserver.domain.member.dto.SignUpRequestDto;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.global.util.jwt.service.CookieService;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthRestController {
    private final AuthService authService;
    private final CookieService cookieService;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenizer jwtTokenizer;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        authService.signUp(signUpRequestDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDTO, HttpServletResponse response) {

        // 인증 처리
        String[] tokens = authService.login(loginRequestDTO.getAccountId(), loginRequestDTO.getPassword(), loginRequestDTO.isRememberMe());
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        // 쿠키 추가
        cookieService.addCookie(response, "accessToken", accessToken, (int) (JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
        cookieService.addRefreshToken(response, "refreshToken", refreshToken, (int) (JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000));

        // 로그인 성공한 사용자 정보 응답
        Member member = memberService.findByLoginId(loginRequestDTO.getAccountId());

        MemberLoginResponseDto memberLoginResponseDto = MemberLoginResponseDto.createMemberLoginResponseDto(String.valueOf(member.getLoginId()), member.getNickname(), member.getProfileUrl(), member.getBackgroundUrl(), member.getBio());
        LoginResponseDto loginResponseDto = LoginResponseDto.createLoginResponseDto(accessToken, memberLoginResponseDto);
        log.info("로그인 성공 : " + member.getLoginId());
        return ResponseEntity.ok(loginResponseDto);
    }
    @PostMapping("/signout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.getCookieValue(request, "refreshToken");
        authService.logout(refreshToken);

        cookieService.deleteCookie(response, "accessToken");
        cookieService.deleteCookie(response, "refreshToken");

        return ResponseEntity.ok("로그아웃되었습니다.");
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // Step 1: 쿠키에서 리프레시 토큰 추출
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("리프레시 토큰이 없습니다.");
        }

        // Step 2: 리프레시 토큰 유효성 검증
        if (!refreshTokenService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("리프레시 토큰이 유효하지 않습니다.");
        }

        // Step 3: 새로운 액세스 토큰 발급
        String loginId = refreshTokenService.getUserIdFromRefreshToken(refreshToken); // 사용자 ID 추출
        Member member = memberService.findByLoginId(loginId);
        String newAccessToken = jwtTokenizer.createAccessToken(member.getKokoaId(), member.getLoginId(), member.getRole().name());

        // Step 4: 새로운 액세스 토큰 반환
        return ResponseEntity.ok()
                .body(Map.of("accessToken", newAccessToken));
    }


}
