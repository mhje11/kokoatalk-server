package org.kokoatalkserver.global.util.config.chatConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisPublisher {
    @Qualifier("chatRoomRedisTemplate")
    private final RedisTemplate<String, ChatMessageRedis> redisTemplate;

    public void publish(ChannelTopic channelTopic, ChatMessageRedis message) {
        log.info("published topic = {}", channelTopic.getTopic());
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
