package org.kokoatalkserver.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberLoginResponseDto {
    private final String accountId;
    private final String nickname;
    private final String profileImageUrl;
    private final String backgroundImageUrl;
    private final String bio;

}
