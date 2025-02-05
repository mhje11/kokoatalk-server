package org.kokoatalkserver.domain.chatMessage.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageGetDto {
    private final LocalDateTime lastCreatedAt;
}
