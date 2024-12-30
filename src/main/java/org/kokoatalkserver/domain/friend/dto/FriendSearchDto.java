package org.kokoatalkserver.domain.friend.dto;

import lombok.Getter;

@Getter
public class FriendSearchDto {
    private String friendCode;

    public FriendSearchDto(String friendCode) {
        this.friendCode = friendCode;
    }
}
