package org.kokoatalkserver.domain.chatRoom.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomCreateDto;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomInfoDto;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomLeaveDto;
import org.kokoatalkserver.domain.chatRoom.service.ChatRoomService;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatRoom")
public class ChatRoomRestController {
    private final ChatRoomService chatRoomService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/create")
    public ResponseEntity<String> createChatRoom(@RequestBody ChatRoomCreateDto chatRoomCreateDto) {
        chatRoomService.createChatRoom(chatRoomCreateDto.getRoomName(), chatRoomCreateDto.getFriendCodeList());
        return ResponseEntity.ok("채팅방 생성 완료");
    }

    @GetMapping("/list")
    public ResponseEntity<List<ChatRoomInfoDto>> getChatRoomList(HttpServletRequest request) {
        String accountId = refreshTokenService.getAccountId(request);
        List<ChatRoomInfoDto> roomList = chatRoomService.getRoomList(accountId);
        return ResponseEntity.ok(roomList);
    }

    @PostMapping("/leave")
    public ResponseEntity<String> leaveRoom(HttpServletRequest request, @RequestBody ChatRoomLeaveDto chatRoomLeaveDto) {
        String accountId = refreshTokenService.getAccountId(request);
        chatRoomService.leaveRoom(accountId, chatRoomLeaveDto.getRoomId());
        return ResponseEntity.ok("방 떠나기 성공");
    }
}
