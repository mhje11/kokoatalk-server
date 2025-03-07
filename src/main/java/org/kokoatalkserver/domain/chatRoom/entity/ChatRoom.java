package org.kokoatalkserver.domain.chatRoom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.ChatRoomParticipant.entity.ChatRoomParticipant;
import org.kokoatalkserver.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public ChatRoom convertToGroupChat(List<Member> participants) {
        this.chatRoomType = ChatRoomType.GROUP;
        this.roomName = generateGroupChatRoomName(participants);
        return this;
    }

    private String generateGroupChatRoomName(List<Member> participants) {
        if (participants.size() <= 3) {
            return participants.stream()
                    .map(Member::getNickname)
                    .collect(Collectors.joining(", "));
        } else {
            return participants.stream()
                    .limit(3)
                    .map(Member::getNickname)
                    .collect(Collectors.joining(", ")) +
                    String.format(" 외 %d명", participants.size() - 3);
        }
    }

    public boolean isEmpty(List<ChatRoomParticipant> participants) {
        return participants.isEmpty();
    }

    public boolean isGroupChat() {
        return this.chatRoomType == ChatRoomType.GROUP;
    }


}
