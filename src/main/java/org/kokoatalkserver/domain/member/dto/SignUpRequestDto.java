package org.kokoatalkserver.domain.member.dto;

import lombok.Getter;

@Getter
public class SignUpRequestDto {
    private String loginId;
    private String password;
}
