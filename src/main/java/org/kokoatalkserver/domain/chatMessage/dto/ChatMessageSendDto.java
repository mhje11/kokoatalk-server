package org.kokoatalkserver.domain.chatMessage.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatMessageSendDto {
    String roomId;
    String message;
    List<String> imageUrls;
}
