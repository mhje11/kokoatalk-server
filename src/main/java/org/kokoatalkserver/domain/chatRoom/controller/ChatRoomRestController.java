package org.kokoatalkserver.domain.chatRoom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatRoom.dto.AddMemberDto;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomCreateDto;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomInfoDto;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomLeaveDto;
import org.kokoatalkserver.domain.chatRoom.service.ChatRoomService;
import org.kokoatalkserver.global.util.jwt.service.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatRoom")
@Tag(name = "ChatRoom", description = "채팅방 관련 API")
public class ChatRoomRestController {
    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 생성", description = "초대할 회원을 선택해 채팅방 생성")
    @ApiResponse(responseCode = "200", description = "채팅방 생성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/create")
    public ResponseEntity<String> createChatRoom(@RequestBody ChatRoomCreateDto chatRoomCreateDto) {
        Long roomId = chatRoomService.createChatRoom(chatRoomCreateDto.getRoomName(), chatRoomCreateDto.getFriendCodeList());
        return ResponseEntity.ok("채팅방 생성 완료 roomId: " + roomId);
    }

    @Operation(summary = "채팅방 목록 조회", description = "유저의 채팅방 목록을 조회")
    @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ChatRoomInfoDto.class))))
    @GetMapping("/list")
    public ResponseEntity<List<ChatRoomInfoDto>> getChatRoomList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<ChatRoomInfoDto> roomList = chatRoomService.getRoomList(customUserDetails.getUserId());
        return ResponseEntity.ok(roomList);
    }

    @Operation(summary = "채팅방 떠나기", description = "채팅방 나가기, 해당 채팅방의 인원이 0명이 되면 채팅방 삭제")
    @ApiResponse(responseCode = "200", description = "채팅방 나가기", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/leave")
    public ResponseEntity<String> leaveRoom(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody ChatRoomLeaveDto chatRoomLeaveDto) {
        chatRoomService.leaveRoom(customUserDetails.getUserId(), chatRoomLeaveDto.getRoomId());
        return ResponseEntity.ok("방 떠나기 성공");
    }

    @Operation(summary = "채팅방에 멤버 초대", description = "채팅방 멤버초대, 1 : 1 채팅방일경우 그룹 채팅방으로 새로운 채팅방 생성")
    @ApiResponse(responseCode = "200", description = "채팅방 멤버 초대 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/addMember")
    public ResponseEntity<String> addMember(@RequestBody AddMemberDto addMemberDto) {
        chatRoomService.checkRoomType(addMemberDto.getRoomId(), addMemberDto.getNewFriendCode());
        return ResponseEntity.ok("멤버 초대 성공");
    }
}
