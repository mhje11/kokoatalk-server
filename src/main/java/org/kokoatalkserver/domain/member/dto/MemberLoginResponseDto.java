package org.kokoatalkserver.domain.member.dto;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberLoginResponseDto {
    private final String accountId;
    private final String nickname;
    private final String profileImageUrl;
    private final String backgroundImageUrl;
    private final String bio;

    public static MemberLoginResponseDto createMemberLoginResponseDto (String accountId, String nickname, String profileImageUrl, String backgroundImageUrl, String bio) {
        return new MemberLoginResponseDto(accountId, nickname, profileImageUrl, backgroundImageUrl, bio);
    }


}
