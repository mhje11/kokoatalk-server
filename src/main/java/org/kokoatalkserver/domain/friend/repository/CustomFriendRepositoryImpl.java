package org.kokoatalkserver.domain.friend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.kokoatalkserver.domain.friend.entity.Friend;
import org.kokoatalkserver.domain.friend.entity.QFriend;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.kokoatalkserver.domain.friend.entity.QFriend.friend1;

@Repository
public class CustomFriendRepositoryImpl implements CustomFriendRepository{
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomFriendRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Friend> findFriendsWithMembers(Long memberId) {
        queryFactory = new JPAQueryFactory(em);
        return queryFactory
                .selectFrom(friend1)
                .join(friend1.friend).fetchJoin()
                .where(friend1.member.kokoaId.eq(memberId))
                .fetch();
    }
}
