package org.kokoatalkserver.domain.ChatRoomParticipant.repository;

import org.kokoatalkserver.domain.ChatRoomParticipant.entity.ChatRoomParticipant;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomWithParticipantsDto;

import java.util.List;

public interface CustomChatRoomParticipantRepository {
    List<ChatRoomWithParticipantsDto> findChatRoomsByMemberId(Long memberId);

}
