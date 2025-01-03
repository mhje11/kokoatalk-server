package org.kokoatalkserver.domain.chatRoom.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomCreateDto {
    private final String roomName;
    private final List<String> friendCodeList;
}
