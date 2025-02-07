package org.kokoatalkserver.domain.friend.repository;

import org.kokoatalkserver.domain.friend.entity.Friend;

import java.util.List;

public interface CustomFriendRepository {
    List<Friend> findFriendsWithMembers(Long memberId);
}
