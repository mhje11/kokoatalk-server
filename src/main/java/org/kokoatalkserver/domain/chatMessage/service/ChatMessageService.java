package org.kokoatalkserver.domain.chatMessage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageScrollDto;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    @Qualifier("chatRoomRedisTemplate")
    private final RedisTemplate<String, ChatMessageRedis> redisTemplate;

    public void saveMessage(String roomId, ChatMessageRedis message) {
        String key = "chat:room:" + roomId;

        log.info("Saving message to Redis. Key: {}, Message: {}", key, message);

        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
        log.info("Message saved to Redis. Key: {}", key);

    }

    public List<ChatMessageScrollDto> getMessageFromRedis(String roomId, LocalDateTime lastCreatedAt, int size) {
        String redisKey = "chat:room:" + roomId;

        List<ChatMessageRedis> redisMessages = redisTemplate.opsForList().range(redisKey, 0, -1);

        if (redisMessages == null || redisMessages.isEmpty()) {
            return Collections.emptyList();
        }

        return redisMessages.stream()
                .map(obj -> (ChatMessageRedis) obj)
                .filter(msg -> msg.getCreated_at().isBefore(lastCreatedAt))
                .sorted(Comparator.comparing(ChatMessageRedis::getCreated_at).reversed())
                .limit(size)
                .map(msg -> ChatMessageScrollDto.createDto(msg.getSenderName(), msg.getMessage(), msg.getCreated_at(), msg.getImageUrls()))
                .collect(Collectors.toList());
    }

}
