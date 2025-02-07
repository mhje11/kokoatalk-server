package org.kokoatalkserver.domain.ChatRoomParticipant.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.kokoatalkserver.domain.ChatRoomParticipant.entity.ChatRoomParticipant;
import org.kokoatalkserver.domain.ChatRoomParticipant.entity.QChatRoomParticipant;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomWithParticipantsDto;
import org.kokoatalkserver.domain.member.entity.QMember;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.kokoatalkserver.domain.ChatRoomParticipant.entity.QChatRoomParticipant.chatRoomParticipant;
import static org.kokoatalkserver.domain.chatRoom.entity.QChatRoom.chatRoom;
import static org.kokoatalkserver.domain.member.entity.QMember.member;

@Repository
public class CustomChatRoomParticipantRepositoryImpl implements CustomChatRoomParticipantRepository{
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomChatRoomParticipantRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ChatRoomWithParticipantsDto> findChatRoomsByMemberId(Long memberId) {
        queryFactory = new JPAQueryFactory(em);

        return queryFactory
                .select(Projections.constructor(
                        ChatRoomWithParticipantsDto.class,
                        chatRoom.id,
                        chatRoom.roomName,
                        member.friendCode,
                        member.nickname,
                        member.profileUrl,
                        member.backgroundUrl,
                        member.bio
                ))
                .from(chatRoomParticipant)
                .join(chatRoomParticipant.chatRoom, chatRoom)
                .join(chatRoomParticipant.member, member)
                .where(chatRoomParticipant.chatRoom.id.in(
                        queryFactory
                                .select(chatRoomParticipant.chatRoom.id)
                                .from(chatRoomParticipant)
                                .where(chatRoomParticipant.member.kokoaId.eq(memberId))
                ))
                .fetch();
    }
}
