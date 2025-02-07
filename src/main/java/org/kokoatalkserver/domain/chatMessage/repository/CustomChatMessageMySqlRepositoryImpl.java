package org.kokoatalkserver.domain.chatMessage.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageMySql;
import org.kokoatalkserver.domain.chatMessage.entity.QChatMessageMySql;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.kokoatalkserver.domain.chatMessage.entity.QChatMessageMySql.chatMessageMySql;

@Repository
public class CustomChatMessageMySqlRepositoryImpl implements CustomChatMessageMySqlRepository{

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomChatMessageMySqlRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ChatMessageMySql> findOlderMessages(Long roomId, LocalDateTime lastCreatedAt, int size) {
        queryFactory = new JPAQueryFactory(em);
        return queryFactory
                .selectFrom(chatMessageMySql)
                .where(
                        chatMessageMySql.roomId.eq(roomId),
                        chatMessageMySql.createdAt.before(lastCreatedAt)
                )
                .orderBy(chatMessageMySql.createdAt.desc())
                .limit(size)
                .fetch();
    }
}
