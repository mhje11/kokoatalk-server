package org.kokoatalkserver.domain.member.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.member.Service.MemberService;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class MemberController {
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/upload/profileImage")
    public ResponseEntity<String> uploadProfileImage(MultipartFile multipartFile, HttpServletRequest request) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        String accountId = refreshTokenService.getUserIdFromRefreshToken(refreshToken);
        memberService.uploadProfileImage(multipartFile, accountId);

        return ResponseEntity.ok("프로필 이미지 수정 완료");
    }
}
