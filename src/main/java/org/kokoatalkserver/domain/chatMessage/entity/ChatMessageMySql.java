package org.kokoatalkserver.domain.chatMessage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_message")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageMySql {
    @Id
    private String id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "chat_message_images", joinColumns = @JoinColumn(name = "chat_message_id"))
    @Column(name = "image_url")
    @BatchSize(size = 20)
    private List<String> imageUrls;

    public static ChatMessageMySql createEntity(ChatMessageRedis chatMessageRedis) {
        return ChatMessageMySql.builder()
                .id(chatMessageRedis.getId())
                .roomId(Long.valueOf(chatMessageRedis.getRoomId()))
                .senderId(Long.valueOf(chatMessageRedis.getSenderId()))
                .senderName(chatMessageRedis.getSenderName())
                .message(chatMessageRedis.getMessage())
                .createdAt(chatMessageRedis.getCreated_at())
                .imageUrls(chatMessageRedis.getImageUrls())
                .build();
    }
}
