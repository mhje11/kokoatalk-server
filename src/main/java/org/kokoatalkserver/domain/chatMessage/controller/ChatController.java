package org.kokoatalkserver.domain.chatMessage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageSendDto;
import org.kokoatalkserver.domain.chatMessage.service.ChatMessageService;
import org.kokoatalkserver.domain.chatMessage.service.ChatService;
import org.kokoatalkserver.global.util.jwt.service.CustomUserDetails;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/send")
    public void sendMessage(Principal principal, ChatMessageSendDto chatMessageSendDto) {
        Long kokoaId = Long.valueOf(principal.getName());
        chatService.sendMessage(kokoaId, chatMessageSendDto.getRoomId(), chatMessageSendDto.getMessage());
    }
}
