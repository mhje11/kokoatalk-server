package org.kokoatalkserver.domain.chatMessage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageSendDto;
import org.kokoatalkserver.domain.chatMessage.service.ChatMessageService;
import org.kokoatalkserver.domain.chatMessage.service.ChatService;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final RefreshTokenService refreshTokenService;

    @MessageMapping("/chat/send")
    public void sendMessage(HttpServletRequest request, @RequestBody ChatMessageSendDto chatMessageSendDto) {
        String accountId = refreshTokenService.getAccountId(request);
        chatService.sendMessage(accountId, chatMessageSendDto.getRoomId(), chatMessageSendDto.getMessage());
    }
}
