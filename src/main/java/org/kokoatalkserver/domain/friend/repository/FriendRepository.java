package org.kokoatalkserver.domain.friend.repository;

import org.kokoatalkserver.domain.friend.entity.Friend;
import org.kokoatalkserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsByMemberAndFriend(Member member, Member friend);

    List<Friend> findAllByMember(Member member);
}
