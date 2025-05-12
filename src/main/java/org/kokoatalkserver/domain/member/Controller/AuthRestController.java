package org.kokoatalkserver.domain.member.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.member.Service.AuthService;
import org.kokoatalkserver.domain.member.Service.MemberService;
import org.kokoatalkserver.domain.member.dto.*;
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
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthRestController {
    private final AuthService authService;
    private final CookieService cookieService;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenizer jwtTokenizer;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 회원을 등록")
    @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    public ResponseEntity<String> signup(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        authService.signUp(signUpRequestDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/signin")
    @Operation(summary = "로그인", description = "회원 로그인 수행")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDTO, HttpServletResponse response) {

        // 인증 처리
        AuthResponseDto authResponseDto = authService.login(loginRequestDTO.getAccountId(), loginRequestDTO.getPassword(), loginRequestDTO.isRememberMe());
        String accessToken = authResponseDto.getAccessToken();
        String refreshToken = authResponseDto.getRefreshToken();

        // 쿠키 추가
        cookieService.addCookie(response, "accessToken", accessToken, (int) (JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
        cookieService.addRefreshToken(response, "refreshToken", refreshToken, (int) (JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000));

        // 로그인 성공한 사용자 정보 응답
        Member member = authResponseDto.getMember();

        MemberLoginResponseDto memberLoginResponseDto = MemberLoginResponseDto.createMemberLoginResponseDto(String.valueOf(member.getLoginId()), member.getNickname(), member.getProfileUrl(), member.getBackgroundUrl(), member.getBio());
        LoginResponseDto loginResponseDto = LoginResponseDto.createLoginResponseDto(accessToken, memberLoginResponseDto);
        log.info("로그인 성공 : " + member.getLoginId());
        return ResponseEntity.ok(loginResponseDto);
    }
    @PostMapping("/signout")
    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.getCookieValue(request, "refreshToken");
        authService.logout(refreshToken);

        cookieService.deleteCookie(response, "accessToken");
        cookieService.deleteCookie(response, "refreshToken");

        return ResponseEntity.ok("로그아웃되었습니다.");
    }
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰 발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "새로운 액세스 토큰 발급 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰이 유효하지 않음")
    })
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
