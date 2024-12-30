package org.kokoatalkserver.domain.member.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.member.Service.MemberService;
import org.kokoatalkserver.domain.member.dto.BioUpdateDto;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberRestController {
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;

    @PutMapping("/upload/profileImage")
    public ResponseEntity<String> uploadProfileImage(MultipartFile multipartFile, HttpServletRequest request) {
        String accountId = refreshTokenService.getAccountId(request);
        memberService.uploadProfileImage(multipartFile, accountId);

        return ResponseEntity.ok("프로필 이미지 수정 완료");
    }

    @PutMapping("/upload/backgroundImage")
    public ResponseEntity<String> uploadBackgroundImage(MultipartFile multipartFile, HttpServletRequest request) {
        String accountId = refreshTokenService.getAccountId(request);
        memberService.uploadBackgroundImage(multipartFile, accountId);

        return ResponseEntity.ok("배경 이미지 수정 완료");
    }

    @PutMapping("/delete/profileImage")
    public ResponseEntity<String> deleteProfileImage(HttpServletRequest request) {
        String accountId = refreshTokenService.getAccountId(request);
        memberService.deleteProfileImage(accountId);

        return ResponseEntity.ok("프로필 이미지 삭제 완료");
    }

    @PutMapping("/delete/backgroundImage")
    public ResponseEntity<String> deleteBackgroundImage(HttpServletRequest request) {
        String accountId = refreshTokenService.getAccountId(request);
        memberService.deleteBackgroundImage(accountId);

        return ResponseEntity.ok("배경 이미지 삭제 완료");
    }

    @PutMapping("/update/bio")
    public ResponseEntity<String> updateBio(HttpServletRequest request, @RequestBody BioUpdateDto bioUpdateDto) {
        String accountId = refreshTokenService.getAccountId(request);
        memberService.updateBio(bioUpdateDto.getBio(), accountId);

        return ResponseEntity.ok("자기소개 수정 완료");
    }



}
