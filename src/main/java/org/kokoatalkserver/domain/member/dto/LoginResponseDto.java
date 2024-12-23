package org.kokoatalkserver.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    private String loginId;
    private String accessToken;
    private String refreshToken;
    private String nickname;
    private String profileUrl;
    private String backgroundUrl;
    private String bio;
}
