package org.kokoatalkserver.domain.chatMessage.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageScrollDto {
    private final String senderName;
    private final String message;
    private final LocalDateTime createdAt;

    public static ChatMessageScrollDto createDto(String senderName, String message, LocalDateTime createdAt) {
        return new ChatMessageScrollDto(senderName, message, createdAt);
    }
}
