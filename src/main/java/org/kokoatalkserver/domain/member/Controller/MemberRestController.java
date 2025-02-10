package org.kokoatalkserver.domain.member.Controller;

import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.member.Service.MemberService;
import org.kokoatalkserver.domain.member.dto.BioUpdateDto;
import org.kokoatalkserver.global.util.jwt.service.CustomUserDetails;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberRestController {
    private final MemberService memberService;

    @PutMapping("/upload/profileImage")
    public ResponseEntity<String> uploadProfileImage(MultipartFile multipartFile, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberService.uploadProfileImage(multipartFile, customUserDetails.getUserId());

        return ResponseEntity.ok("프로필 이미지 수정 완료");
    }

    @PutMapping("/upload/backgroundImage")
    public ResponseEntity<String> uploadBackgroundImage(MultipartFile multipartFile, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberService.uploadBackgroundImage(multipartFile, customUserDetails.getUserId());

        return ResponseEntity.ok("배경 이미지 수정 완료");
    }

    @PutMapping("/delete/profileImage")
    public ResponseEntity<String> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberService.deleteProfileImage(customUserDetails.getUserId());

        return ResponseEntity.ok("프로필 이미지 삭제 완료");
    }

    @PutMapping("/delete/backgroundImage")
    public ResponseEntity<String> deleteBackgroundImage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberService.deleteBackgroundImage(customUserDetails.getUserId());

        return ResponseEntity.ok("배경 이미지 삭제 완료");
    }

    @PutMapping("/update/bio")
    public ResponseEntity<String> updateBio(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BioUpdateDto bioUpdateDto) {
        memberService.updateBio(bioUpdateDto.getBio(), customUserDetails.getUserId());

        return ResponseEntity.ok("자기소개 수정 완료");
    }

}
