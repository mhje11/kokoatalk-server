package org.kokoatalkserver.domain.chatRoom.repository;

import org.kokoatalkserver.domain.chatRoom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
