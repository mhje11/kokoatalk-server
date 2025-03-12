package org.kokoatalkserver.domain.member.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Member", description = "회원 관련 API")
public class MemberRestController {
    private final MemberService memberService;

    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식"),
    })
    @PutMapping("/upload/profileImage")
    public ResponseEntity<String> uploadProfileImage(MultipartFile multipartFile, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberService.uploadProfileImage(multipartFile, customUserDetails.getUserId());

        return ResponseEntity.ok("프로필 이미지 수정 완료");
    }

    @Operation(summary = "배경 이미지 업로드", description = "사용자의 배경 이미지를 업로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식")
    })
    @PutMapping("/upload/backgroundImage")
    public ResponseEntity<String> uploadBackgroundImage(MultipartFile multipartFile, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberService.uploadBackgroundImage(multipartFile, customUserDetails.getUserId());

        return ResponseEntity.ok("배경 이미지 수정 완료");
    }

    @Operation(summary = "프로필 이미지 삭제", description = "사용자의 프로필 이미지를 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 이미지 삭제 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "기본 프로필 이미지는 삭제 불가")

    })
    @PutMapping("/delete/profileImage")
    public ResponseEntity<String> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberService.deleteProfileImage(customUserDetails.getUserId());

        return ResponseEntity.ok("프로필 이미지 삭제 완료");
    }

    @Operation(summary = "배경 이미지 삭제", description = "사용자의 배경 이미지를 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배경 이미지 삭제 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "기본 배경 이미지는 삭제 불가")

    })
    @PutMapping("/delete/backgroundImage")
    public ResponseEntity<String> deleteBackgroundImage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberService.deleteBackgroundImage(customUserDetails.getUserId());

        return ResponseEntity.ok("배경 이미지 삭제 완료");
    }

    @Operation(summary = "자기소개 수정", description = "사용자의 자기소개를 수정")
    @ApiResponse(responseCode = "200", description = "자기소개 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PutMapping("/update/bio")
    public ResponseEntity<String> updateBio(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BioUpdateDto bioUpdateDto) {
        memberService.updateBio(bioUpdateDto.getBio(), customUserDetails.getUserId());

        return ResponseEntity.ok("자기소개 수정 완료");
    }

}
