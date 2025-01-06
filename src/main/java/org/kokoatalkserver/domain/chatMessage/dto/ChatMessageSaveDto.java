package org.kokoatalkserver.domain.chatMessage.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageSaveDto {
    private String roomId;
    private String senderId;
    private String senderNickname;
    private String message;

    public static ChatMessageSaveDto createChatMessageSaveDto(String roomId, String senderId, String senderNickname, String message) {
        return new ChatMessageSaveDto(roomId, senderId, senderNickname, message);
    }
}
