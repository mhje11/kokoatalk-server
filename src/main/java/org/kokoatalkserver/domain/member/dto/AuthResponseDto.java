package org.kokoatalkserver.domain.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kokoatalkserver.domain.member.entity.Member;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponseDto {
    private final String accessToken;
    private final String refreshToken;
    private final Member member;

    public static AuthResponseDto createAuthResponseDto(String accessToken, String refreshToken, Member member) {
        return new AuthResponseDto(accessToken, refreshToken, member);
    }
}
