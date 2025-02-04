package org.kokoatalkserver.domain.chatMessage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_message")
@Getter
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
    private List<String> imageUrls;

    public static ChatMessageMySql createEntity(String id, Long roomId, Long senderId, String senderName, String message, LocalDateTime createdAt, List<String> imageUrls) {
        return new ChatMessageMySql(id, roomId, senderId, senderName, message, createdAt, imageUrls);
    }
}
