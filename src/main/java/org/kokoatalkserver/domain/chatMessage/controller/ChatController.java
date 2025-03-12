package org.kokoatalkserver.domain.chatMessage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageGetDto;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageScrollDto;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageSendDto;
import org.kokoatalkserver.domain.chatMessage.service.ChatMessageService;
import org.kokoatalkserver.domain.chatMessage.service.ChatService;
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
@Tag(name = "Chat", description = "채팅 관련 API")
public class ChatController {
    private final ChatService chatService;
    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅 메시지 전송 (WebSocket)",
    description = "WebSocket을 사용하여 특정 채팅방에 메시지를 전송" + "\n" +
            "해당 엔드포인트는 WebSocket 연결이 필요하며, STOMP 프로토콜을 사용해야 함")
    @PostMapping("/api/chat/websocket-docs")
    public ResponseEntity<String> websocketDocs() {
        return ResponseEntity.ok("Swagger WebSocket 사용법을 표시하기 위한 API");
    }

    @MessageMapping("/chat/send/{roomId}")
    public void sendMessage(Principal principal, ChatMessageSendDto chatMessageSendDto, @DestinationVariable String roomId) {
        Long kokoaId = Long.valueOf(principal.getName());
        chatService.sendMessage(kokoaId, roomId, chatMessageSendDto.getMessage(), chatMessageSendDto.getImageUrls());
    }

    @Operation(summary = "채팅 이미지 업로드", description = "채팅에서 사용할 이미지를 업로드")
    @ApiResponse(responseCode = "200", description = "채팅방에서 사용할 이미지를 업로드 하는 기능", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class))))
    @PostMapping("/api/chat/image/upload")
    public ResponseEntity<List<String>> uploadChatImages(@RequestPart List<MultipartFile> multipartFile) {
        List<String> imageUrls = chatService.uploadFiles(multipartFile);
        return ResponseEntity.ok(imageUrls);
    }

    @Operation(summary = "채팅 메시지 조회", description = "특정 채팅방의 메시지를 가져오며 무한 스크롤 방식 이용을 위한 API")
    @ApiResponse(responseCode = "200", description = "채팅방 메시지 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ChatMessageScrollDto.class))))
    @GetMapping("/api/chatRoom/{roomId}/messages")
    public ResponseEntity<List<ChatMessageScrollDto>> getChatMessages(@RequestBody ChatMessageGetDto chatMessageGetDto, @PathVariable String roomId) {
        LocalDateTime lastCreatedAt = chatMessageGetDto.getLastCreatedAt() != null ? chatMessageGetDto.getLastCreatedAt() : LocalDateTime.now();
        List<ChatMessageScrollDto> messages = chatMessageService.getMessage(roomId, lastCreatedAt, 20);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "채팅 메시지 백업", description = "채팅 메시지를 Redis 에서 MySql 로 백업, 실제로는 스케줄러를 사용함")
    @ApiResponse(responseCode = "200", description = "백업 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/api/chatMessage/backup")
    public ResponseEntity<String> triggerBackup() {
        chatMessageService.archiveChatMessages();
        return ResponseEntity.ok("백업 실행");
    }
}
