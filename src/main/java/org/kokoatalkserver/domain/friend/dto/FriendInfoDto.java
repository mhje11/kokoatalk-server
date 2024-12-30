package org.kokoatalkserver.domain.friend.dto;

import lombok.Getter;
import org.kokoatalkserver.domain.member.entity.Member;

@Getter
public class FriendInfoDto {
    private String friendCode;
    private String nickname;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private String bio;

    public static FriendInfoDto toDto(Member member) {
        return new FriendInfoDto(member.getFriendCode(), member.getNickname(), member.getProfileUrl(), member.getBackgroundUrl(), member.getBio());
    }

    private FriendInfoDto(String friendCode, String nickname, String profileImageUrl, String backgroundImageUrl, String bio) {
        this.friendCode = friendCode;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.bio = bio;
    }
}
