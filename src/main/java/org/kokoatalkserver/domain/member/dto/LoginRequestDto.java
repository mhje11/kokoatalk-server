package org.kokoatalkserver.domain.member.dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String loginId;
    private String password;
    private boolean rememberMe;
}
