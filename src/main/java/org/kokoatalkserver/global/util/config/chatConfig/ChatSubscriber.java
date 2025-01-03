package org.kokoatalkserver.global.util.config.chatConfig;

import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChatSubscriber {
    public void handleMessage(ChatMessageRedis messageRedis, String channel) {
        log.info("채널 : " + channel + ", 메시지 : " + messageRedis);
    }
}
