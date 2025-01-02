package org.kokoatalkserver.domain.friend.dto;

import lombok.Getter;

@Getter
public class FriendSearchDto {
    private final String friendCode;

    public FriendSearchDto(String friendCode) {
        this.friendCode = friendCode;
    }
}
