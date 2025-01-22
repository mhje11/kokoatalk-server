package org.kokoatalkserver.domain.chatMessage.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageScrollDto {
    private final String senderName;
    private final String message;
    private final LocalDateTime createdAt;
    private final List<String> imageUrls;

    public static ChatMessageScrollDto createDto(String senderName, String message, LocalDateTime createdAt, List<String> imageUrls) {
        return new ChatMessageScrollDto(senderName, message, createdAt, imageUrls);
    }
}
