package org.kokoatalkserver.domain.friend.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.kokoatalkserver.domain.member.entity.Member;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FriendInfoDto {
    private final String friendCode;
    private final String nickname;
    private final String profileImageUrl;
    private final String backgroundImageUrl;
    private final String bio;

    public static FriendInfoDto fromMember(Member member) {
        return FriendInfoDto.builder()
                .friendCode(member.getFriendCode())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileUrl())
                .backgroundImageUrl(member.getBackgroundUrl())
                .bio(member.getBio())
                .build();
    }

}
