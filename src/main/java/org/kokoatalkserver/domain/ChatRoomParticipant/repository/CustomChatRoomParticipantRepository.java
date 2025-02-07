package org.kokoatalkserver.domain.ChatRoomParticipant.repository;

import org.kokoatalkserver.domain.ChatRoomParticipant.entity.ChatRoomParticipant;

import java.util.List;

public interface CustomChatRoomParticipantRepository {
    List<ChatRoomParticipant> findChatRoomsByMemberId(Long memberId);

}
