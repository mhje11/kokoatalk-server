package org.kokoatalkserver.domain.chatMessage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageScrollDto;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageMySql;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.kokoatalkserver.domain.chatMessage.repository.ChatMessageMySqlRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private static final int BATCH_SIZE = 100;

    @Qualifier("chatRoomRedisTemplate")
    private final RedisTemplate<String, ChatMessageRedis> redisTemplate;

    private final ChatMessageMySqlRepository chatMessageMySqlRepository;

    public void saveMessage(String roomId, ChatMessageRedis message) {
        String key = "chat:room:" + roomId;

        log.info("Saving message to Redis. Key: {}, Message: {}", key, message);

        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
        log.info("Message saved to Redis. Key: {}", key);
    }

    public void temporaryMessage() {
        String key = "chat:room:" + "4";
        ChatMessageRedis message = ChatMessageRedis.create("4", "6", "kokoa4", "ngrinder 메시지 테스트" + UUID.randomUUID(), List.of());
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    public List<ChatMessageScrollDto> getMessage(String roomId, LocalDateTime lastCreatedAt, int size) {
        String redisKey = "chat:room:" + roomId;

        List<ChatMessageRedis> redisMessages = redisTemplate.opsForList().range(redisKey, 0, -1);
        List<ChatMessageScrollDto> redisResult = new ArrayList<>();

        if (redisMessages != null || !redisMessages.isEmpty()) {
            redisResult = redisMessages.stream()
                    .filter(msg -> msg.getCreated_at().isBefore(lastCreatedAt))
                    .sorted(Comparator.comparing(ChatMessageRedis::getCreated_at).reversed())
                    .limit(size)
                    .map(msg -> ChatMessageScrollDto.createDto(
                            msg.getSenderName(), msg.getMessage(), msg.getCreated_at(), msg.getImageUrls()
                    )).collect(Collectors.toList());
        }
        if (redisResult.size() < size) {
            int remainingSize = size - redisResult.size();
            List<ChatMessageMySql> olderMessages = chatMessageMySqlRepository.findOlderMessages(Long.valueOf(roomId), lastCreatedAt, remainingSize);

            List<ChatMessageScrollDto> mySqlResult = olderMessages.stream()
                    .map(msg -> ChatMessageScrollDto.createDto(
                            msg.getSenderName(), msg.getMessage(), msg.getCreatedAt(), msg.getImageUrls()
                    ))
                    .collect(Collectors.toList());

            redisResult.addAll(mySqlResult);
        }

        return redisResult;
    }

    public void archiveChatMessages() {
        Set<String> keys = redisTemplate.keys("chat:room:*");

        if (keys == null || keys.isEmpty()) {
            log.info("레디스 키 없음");
            return;
        }

        keys.forEach(key -> {
            try {
                List<ChatMessageRedis> messages = redisTemplate.opsForList().range(key, 0, -1);
                if (messages != null && !messages.isEmpty()) {
                    saveMessagesToDataBase(messages);
                    redisTemplate.delete(key);
                    log.info("백업 및 삭제 완료 - key : {}", key);
                }
            } catch (Exception e) {
                log.error("백업중 오류 발생 - key: {}, error: {}", key, e.getMessage());
            }

        });
    }

    @Transactional
    protected void saveMessagesToDataBase(List<ChatMessageRedis> messages) {
        for (int i = 0; i < messages.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, messages.size());
            List<ChatMessageRedis> batch = messages.subList(i, end);

            List<ChatMessageMySql> messageEntities = batch.stream()
                    .map(ChatMessageMySql::createEntity)
                    .collect(Collectors.toList());

            chatMessageMySqlRepository.saveAll(messageEntities);
        }
    }

}
