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

    @MessageMapping("/chat/send")
    public void sendMessage(Principal principal, ChatMessageSendDto chatMessageSendDto) {
        Long kokoaId = Long.valueOf(principal.getName());
        chatService.sendMessage(kokoaId, chatMessageSendDto.getRoomId(), chatMessageSendDto.getMessage(), chatMessageSendDto.getImageUrls());
    }
    @PostMapping("/api/chat/image/upload")
    public ResponseEntity<List<String>> uploadChatImages(@RequestPart List<MultipartFile> multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException(ExceptionCode.INVALID_FILE_FORMAT);
        }
        List<String> imageUrls = chatService.uploadFiles(multipartFile);
        return ResponseEntity.ok(imageUrls);
    }

    @GetMapping("/api/chatRoom/messages")
    public ResponseEntity<List<ChatMessageScrollDto>> getChatMessages(@RequestBody ChatMessageGetDto chatMessageGetDto) {
        LocalDateTime lastCreatedAt = chatMessageGetDto.getLastCreatedAt() != null ? chatMessageGetDto.getLastCreatedAt() : LocalDateTime.now();
        List<ChatMessageScrollDto> messages = chatMessageService.getMessageFromRedis(chatMessageGetDto.getRoomId(), lastCreatedAt, 20);
        return ResponseEntity.ok(messages);
    }
}
