package org.kokoatalkserver.domain.chatRoom.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatRoom.dto.AddMemberDto;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomCreateDto;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomInfoDto;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomLeaveDto;
import org.kokoatalkserver.domain.chatRoom.service.ChatRoomService;
import org.kokoatalkserver.global.util.jwt.service.CustomUserDetails;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatRoom")
public class ChatRoomRestController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ResponseEntity<String> createChatRoom(@RequestBody ChatRoomCreateDto chatRoomCreateDto) {
        chatRoomService.createChatRoom(chatRoomCreateDto.getRoomName(), chatRoomCreateDto.getFriendCodeList());
        return ResponseEntity.ok("채팅방 생성 완료");
    }

    @GetMapping("/list")
    public ResponseEntity<List<ChatRoomInfoDto>> getChatRoomList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<ChatRoomInfoDto> roomList = chatRoomService.getRoomList(customUserDetails.getUserId());
        return ResponseEntity.ok(roomList);
    }

    @PostMapping("/leave")
    public ResponseEntity<String> leaveRoom(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody ChatRoomLeaveDto chatRoomLeaveDto) {
        chatRoomService.leaveRoom(customUserDetails.getUserId(), chatRoomLeaveDto.getRoomId());
        return ResponseEntity.ok("방 떠나기 성공");
    }

    @PostMapping("/addMember")
    public ResponseEntity<String> addMember(@RequestBody AddMemberDto addMemberDto) {
        chatRoomService.checkRoomType(addMemberDto.getRoomId(), addMemberDto.getNewFriendCode());
        return ResponseEntity.ok("멤버 초대 성공");
    }
}
