package org.kokoatalkserver.domain.chatMessage.controller;

import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageGetDto;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageScrollDto;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageSendDto;
import org.kokoatalkserver.domain.chatMessage.service.ChatMessageService;
import org.kokoatalkserver.domain.chatMessage.service.ChatService;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/send/{roomId}")
    public void sendMessage(Principal principal, ChatMessageSendDto chatMessageSendDto, @DestinationVariable String roomId) {
        Long kokoaId = Long.valueOf(principal.getName());
        chatService.sendMessage(kokoaId, roomId, chatMessageSendDto.getMessage(), chatMessageSendDto.getImageUrls());
    }
    @PostMapping("/api/chat/image/upload")
    public ResponseEntity<List<String>> uploadChatImages(@RequestPart List<MultipartFile> multipartFile) {
        List<String> imageUrls = chatService.uploadFiles(multipartFile);
        return ResponseEntity.ok(imageUrls);
    }

    @GetMapping("/api/chatRoom/{roomId}/messages")
    public ResponseEntity<List<ChatMessageScrollDto>> getChatMessages(@RequestBody ChatMessageGetDto chatMessageGetDto, @PathVariable String roomId) {
        LocalDateTime lastCreatedAt = chatMessageGetDto.getLastCreatedAt() != null ? chatMessageGetDto.getLastCreatedAt() : LocalDateTime.now();
        List<ChatMessageScrollDto> messages = chatMessageService.getMessageFromRedis(roomId, lastCreatedAt, 20);
        return ResponseEntity.ok(messages);
    }
}
