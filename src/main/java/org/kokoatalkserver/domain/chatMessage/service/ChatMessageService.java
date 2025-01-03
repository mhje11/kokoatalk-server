package org.kokoatalkserver.domain.chatMessage.service;

import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveMessage(String roomId, ChatMessageRedis message) {
        String key = "chat:room:" + roomId;
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    public List<Object> getRecentMessages(String roomId) {
        String key = "chat:room:" + roomId;
        return redisTemplate.opsForList().range(key, -20, -1);
    }

}
