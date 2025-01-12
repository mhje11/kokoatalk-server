package org.kokoatalkserver.domain.chatMessage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveMessage(String roomId, ChatMessageRedis message) {
        String key = "chat:room:" + roomId;

        log.info("Saving message to Redis. Key: {}, Message: {}", key, message);

        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
        log.info("Message saved to Redis. Key: {}", key);

    }

    public List<Object> getRecentMessages(String roomId) {
        String key = "chat:room:" + roomId;
        return redisTemplate.opsForList().range(key, -20, -1);
    }

}
