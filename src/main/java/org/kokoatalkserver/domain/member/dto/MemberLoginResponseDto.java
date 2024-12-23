package org.kokoatalkserver.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginResponseDto {
    private String accountId;
    private String nickname;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private String bio;

    public MemberLoginResponseDto(String accountId, String nickname, String profileImageUrl, String backgroundImageUrl, String bio) {
        this.accountId = accountId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.bio = bio;
    }
}
