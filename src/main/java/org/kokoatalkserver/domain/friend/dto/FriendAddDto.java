package org.kokoatalkserver.domain.friend.dto;

import lombok.Getter;

@Getter
public class FriendAddDto {
    private final String friendCode;

    public FriendAddDto(String friendCode) {
        this.friendCode = friendCode;
    }
}
