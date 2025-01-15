package org.kokoatalkserver.global.util.config.chatConfig;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisSubscriber implements MessageListener {

    @Qualifier("chatRoomRedisTemplate")
    private final RedisTemplate<String, ChatMessageRedis> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;



    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 데이터 역직렬화
            ChatMessageRedis roomMessage = (ChatMessageRedis) redisTemplate.getValueSerializer().deserialize(message.getBody());
            log.info("Deserialized message: {}", roomMessage.getMessage());

            String formattedMessage = roomMessage.getSenderName() + " : " + roomMessage.getMessage();

            // WebSocket으로 메시지 전송
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getRoomId(), formattedMessage);
        } catch (Exception e) {
            log.error("Error during message processing: ", e);
        }
    }
}
