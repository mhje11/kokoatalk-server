package org.kokoatalkserver.domain.member.Controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.member.Service.AuthService;
import org.kokoatalkserver.domain.member.Service.MemberService;
import org.kokoatalkserver.domain.member.dto.LoginRequestDto;
import org.kokoatalkserver.domain.member.dto.LoginResponseDto;
import org.kokoatalkserver.domain.member.dto.SignUpRequestDto;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.global.util.jwt.service.CookieService;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final AuthService authService;
    private final CookieService cookieService;
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequestDto signUpRequestDto) {
        authService.signUp(signUpRequestDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDTO, HttpServletResponse response) {

        // 인증 처리
        String[] tokens = authService.login(loginRequestDTO.getLoginId(), loginRequestDTO.getPassword());
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        // 쿠키 추가
        cookieService.addCookie(response, "accessToken", accessToken, (int) (JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
        cookieService.addCookie(response, "refreshToken", refreshToken, (int) (JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000));

        // 로그인 성공한 사용자 정보 응답
        Member member = memberService.findByLoginId(loginRequestDTO.getLoginId());
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .loginId(String.valueOf(member.getKokoaId()))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ResponseEntity.ok(loginResponseDto);
    }


}