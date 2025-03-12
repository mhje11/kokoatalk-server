package org.kokoatalkserver.domain.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.friend.dto.FriendAddDto;
import org.kokoatalkserver.domain.friend.dto.FriendInfoDto;
import org.kokoatalkserver.domain.friend.dto.FriendSearchDto;
import org.kokoatalkserver.domain.friend.service.FriendService;
import org.kokoatalkserver.global.util.jwt.service.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
@Tag(name = "Friend", description = "친구 관련 API")
public class FriendRestController {
    private final FriendService friendService;

    @Operation(summary = "친구 검색", description = "친구 코드로 친구 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "친구 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendInfoDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음")
    })
    @PostMapping("/search")
    public ResponseEntity<FriendInfoDto> findFriend(@RequestBody FriendSearchDto friendSearchDto) {
        FriendInfoDto friendInfoDto = friendService.findByFriendCode(friendSearchDto.getFriendCode());
        return ResponseEntity.ok(friendInfoDto);
    }

    @Operation(summary = "친구 추가", description = "친구 코드로 친구 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "친구 추가 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "이미 친구 관계일 경우 친구 추가 실패"),
            @ApiResponse(responseCode = "400", description = "자기 자신은 친구로 추가할 수 없음")
    })
    @PostMapping("/add")
    public ResponseEntity<String> addFriend(@RequestBody FriendAddDto friendAddDto, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        friendService.addFriend(customUserDetails, friendAddDto.getFriendCode());
        return ResponseEntity.ok("친구 추가 완료, 친구 코드 : " + friendAddDto.getFriendCode());
    }

    @Operation(summary = "친구 목록 조회", description = "사용자의 친구 목록 조회")
    @ApiResponse(responseCode = "200", description = "사용자의 친구 목록 조회", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FriendInfoDto.class))))
    @GetMapping("/friendList")
    public ResponseEntity<List<FriendInfoDto>> findFriendList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<FriendInfoDto> friendList = friendService.getFriendList(customUserDetails);
        return ResponseEntity.ok(friendList);
    }

}
