package org.kokoatalkserver.domain.ChatRoomParticipant.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.kokoatalkserver.domain.ChatRoomParticipant.entity.ChatRoomParticipant;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.kokoatalkserver.domain.ChatRoomParticipant.entity.QChatRoomParticipant.chatRoomParticipant;
import static org.kokoatalkserver.domain.chatRoom.entity.QChatRoom.chatRoom;

@Repository
public class CustomChatRoomParticipantRepositoryImpl implements CustomChatRoomParticipantRepository{
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomChatRoomParticipantRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ChatRoomParticipant> findChatRoomsByMemberId(Long memberId) {
        queryFactory = new JPAQueryFactory(em);
        return queryFactory
                .selectFrom(chatRoomParticipant)
                .join(chatRoomParticipant.chatRoom, chatRoom).fetchJoin()
                .where(chatRoomParticipant.member.kokoaId.eq(memberId))
                .fetch();
    }
}
