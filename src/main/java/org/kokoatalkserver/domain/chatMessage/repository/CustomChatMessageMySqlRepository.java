package org.kokoatalkserver.domain.chatMessage.repository;

import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageMySql;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomChatMessageMySqlRepository {
    List<ChatMessageMySql> findOlderMessages(Long roomId, LocalDateTime lastCreatedAt, int size);
}
