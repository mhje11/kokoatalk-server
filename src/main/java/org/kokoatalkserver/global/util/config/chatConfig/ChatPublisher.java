package org.kokoatalkserver.global.util.config.chatConfig;

import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publishMessage(String roomId, ChatMessageRedis message) {
        String topic = "chat:" + roomId;
        redisTemplate.convertAndSend(topic, message);
    }
}
