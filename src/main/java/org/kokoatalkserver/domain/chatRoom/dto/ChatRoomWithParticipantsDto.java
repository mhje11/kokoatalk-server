package org.kokoatalkserver.domain.chatRoom.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ChatRoomWithParticipantsDto {
    private final Long roomId;
    private final String roomName;
    private final String participantFriendCode;
    private final String participantNickname;
    private final String participantProfileUrl;
    private final String participantBackgroundUrl;
    private final String participantBio;
}
