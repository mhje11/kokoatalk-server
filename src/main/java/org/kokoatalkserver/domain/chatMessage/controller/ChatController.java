package org.kokoatalkserver.domain.chatMessage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageSendDto;
import org.kokoatalkserver.domain.chatMessage.service.ChatMessageService;
import org.kokoatalkserver.domain.chatMessage.service.ChatService;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final RefreshTokenService refreshTokenService;

    @MessageMapping("/chat/send")
    @SendTo("/sub/chat/room/{roomId}")
    public void sendMessage(@Header("simpSessionAttributes") Map<String, Object> sessionAttributes,
                            ChatMessageSendDto chatMessageSendDto) {
        Long accountId = (Long) sessionAttributes.get("userId"); // WebSocket 세션에서 userId 가져오기
        chatService.sendMessage(accountId, chatMessageSendDto.getRoomId(), chatMessageSendDto.getMessage());
    }
}
