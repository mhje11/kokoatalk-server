package org.kokoatalkserver.domain.chatMessage.dto;

import lombok.Getter;

@Getter
public class ChatMessageSendDto {
    String roomId;
    String message;
}
