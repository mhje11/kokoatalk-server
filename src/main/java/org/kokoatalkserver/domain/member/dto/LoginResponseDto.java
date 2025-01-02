package org.kokoatalkserver.domain.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginResponseDto {
    private final String accessToken;
    private final MemberLoginResponseDto memberLoginResponseDto;

    public static LoginResponseDto createLoginResponseDto(String accessToken, MemberLoginResponseDto memberLoginResponseDto) {
        return new LoginResponseDto(accessToken, memberLoginResponseDto);
    }
}
