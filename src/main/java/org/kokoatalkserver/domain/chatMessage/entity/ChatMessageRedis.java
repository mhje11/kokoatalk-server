package org.kokoatalkserver.domain.chatMessage.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Builder
@AllArgsConstructor
@RedisHash("chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageRedis {
    @Id
    private String id;

    private String roomId;

    private String senderId;

    private String senderName;

    private String message;

    private LocalDateTime created_at;

    private List<String> imageUrls;

    @TimeToLive
    private Long ttl = TimeUnit.DAYS.toSeconds(7);
}
