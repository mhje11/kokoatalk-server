package org.kokoatalkserver.domain.chatMessage.repository;

import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.springframework.data.repository.CrudRepository;

public interface ChatMessageRedisRepository extends CrudRepository<ChatMessageRedis, String> {
}
