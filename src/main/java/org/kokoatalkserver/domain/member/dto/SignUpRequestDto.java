package org.kokoatalkserver.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class SignUpRequestDto {

    @NotBlank(message = "ID를 입력해주세요")
    @Size(min = 5, max = 20, message = "ID는 5자에서 20자 사이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "ID는 영어와 숫자만 입력 가능합니다.")
    private final String accountId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+$",
            message = "비밀번호는 영어, 숫자, 특수문자를 포함해야 합니다.")
    private final String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 1, max = 20, message = "닉네임은 1자에서 20자 사이여야 합니다.")
    private final String nickname;
}
