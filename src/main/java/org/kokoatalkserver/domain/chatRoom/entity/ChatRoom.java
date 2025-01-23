package org.kokoatalkserver.domain.chatRoom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_room_type")
    private ChatRoomType chatRoomType;


    private ChatRoom(String roomName, ChatRoomType chatRoomType) {
        this.roomName = roomName;
        this.chatRoomType = chatRoomType;
        this.createdAt = LocalDateTime.now();
    }

    public static ChatRoom createChatRoom(String roomName, ChatRoomType chatRoomType) {
        return new ChatRoom(roomName, chatRoomType);
    }


}
