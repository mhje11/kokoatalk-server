package org.kokoatalkserver.domain.friend.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FriendSearchDto {
    private final String friendCode;
}
