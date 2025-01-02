package org.kokoatalkserver.domain.member.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequestDto {
    private final String accountId;
    private final String password;
    private final boolean rememberMe;
}
