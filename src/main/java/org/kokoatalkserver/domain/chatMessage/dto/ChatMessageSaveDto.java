package org.kokoatalkserver.domain.chatMessage.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageSaveDto {
    private String roomId;
    private String senderId;
    private String senderNickname;
    private String message;
    private List<String> imageUrls;

    public static ChatMessageSaveDto createChatMessageSaveDto(String roomId, String senderId, String senderNickname, String message, List<String> imageUrls) {
        return new ChatMessageSaveDto(roomId, senderId, senderNickname, message, imageUrls);
    }
}
