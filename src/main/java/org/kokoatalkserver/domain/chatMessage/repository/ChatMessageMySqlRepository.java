package org.kokoatalkserver.domain.chatMessage.repository;

import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageMySql;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageMySqlRepository extends JpaRepository<ChatMessageMySql, String>, CustomChatMessageMySqlRepository {

}
