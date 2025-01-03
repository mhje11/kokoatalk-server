package org.kokoatalkserver.domain.chatRoom.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kokoatalkserver.domain.friend.dto.FriendInfoDto;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomInfoDto {
    private final Long roomId;
    private final String roomName;
    private final List<FriendInfoDto> participantList;

    public static ChatRoomInfoDto createChatRoomInfoDto(Long roomId, String roomName, List<FriendInfoDto> participantList) {
        return new ChatRoomInfoDto(roomId, roomName, participantList);
    }

}
