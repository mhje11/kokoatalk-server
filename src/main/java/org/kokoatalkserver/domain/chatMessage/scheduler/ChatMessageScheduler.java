package org.kokoatalkserver.domain.chatMessage.scheduler;

import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.service.ChatMessageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageScheduler {

    private final ChatMessageService chatMessageService;

    @Scheduled(fixedRate = 6 * 24 * 60 * 60 * 1000)
    public void backupChatMessages() {
        chatMessageService.archiveChatMessages();
    }
}
