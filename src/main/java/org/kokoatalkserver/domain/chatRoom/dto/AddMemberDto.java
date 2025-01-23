package org.kokoatalkserver.domain.chatRoom.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AddMemberDto {
    private final Long roomId;
    private final List<String> newFriendCode;
}
