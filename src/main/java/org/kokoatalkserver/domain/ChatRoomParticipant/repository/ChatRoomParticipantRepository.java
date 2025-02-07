package org.kokoatalkserver.domain.ChatRoomParticipant.repository;

import org.kokoatalkserver.domain.ChatRoomParticipant.entity.ChatRoomParticipant;
import org.kokoatalkserver.domain.chatRoom.entity.ChatRoom;
import org.kokoatalkserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long>, CustomChatRoomParticipantRepository {
    List<ChatRoomParticipant> findAllByMember(Member member);
   List<ChatRoomParticipant> findAllByChatRoom(ChatRoom chatRoom);
   Optional<ChatRoomParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

   boolean existsByChatRoom(ChatRoom chatRoom);
}
