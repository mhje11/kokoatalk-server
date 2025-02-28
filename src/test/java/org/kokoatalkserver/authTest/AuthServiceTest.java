package org.kokoatalkserver.authTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kokoatalkserver.domain.member.Service.AuthService;
import org.kokoatalkserver.domain.member.dto.SignUpRequestDto;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.jwt.entity.RefreshToken;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenizer jwtTokenizer;

    @Mock
    private RefreshTokenService refreshTokenService;

    private SignUpRequestDto signUpRequestDto;
    private Member testMember;
    private String testAccessToken = "accessToken";
    private String testRefreshToken = "refreshToken";

    @BeforeEach
    void setUp() {
        signUpRequestDto = new SignUpRequestDto("testUser", "password123", "테스트유저");
        testMember = Member.builder()
                .loginId("testUser")
                .password("암호화된 비밀번호")
                .nickname("테스트유저")
                .build();
    }

    @Test
    void 회원가입_성공() {
        //given
        when(memberRepository.findByLoginId(signUpRequestDto.getAccountId())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signUpRequestDto.getPassword())).thenReturn("암호화된 비밀번호");

        //when
        authService.signUp(signUpRequestDto);

        //then
        // verify, times -> 메서드가 몇번 호출됐는지 검증
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void 회원가입_실패_중복아이디() {
        //given
        when(memberRepository.findByLoginId(signUpRequestDto.getAccountId()))
                .thenReturn(Optional.of(testMember));

        //when & then
        assertThrows(CustomException.class, () -> authService.signUp(signUpRequestDto));
    }

    @Test
    void 로그인_성공() {
        //given
        String rawPassword = "password123";
        testMember = Member.builder()
                .loginId("testUser")
                .password("암호화된 비밀번호")
                .nickname("테스트유저")
                .build();
        ReflectionTestUtils.setField(testMember, "kokoaId", 1L);

        when(memberRepository.findByLoginId(testMember.getLoginId())).thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches(rawPassword, testMember.getPassword())).thenReturn(true);

        when(jwtTokenizer.createAccessToken(anyLong(), anyString(), anyString())).thenReturn(testAccessToken);
        when(refreshTokenService.findRefreshTokenByLoginId(testMember.getLoginId())).thenReturn(Optional.empty());
        when(jwtTokenizer.createRefreshToken(anyLong(), anyString(), anyString())).thenReturn(testRefreshToken);

        //when
        String[] tokens = authService.login(testMember.getLoginId(), rawPassword, true);

        //then
        assertNotNull(tokens);
        assertEquals(testAccessToken, tokens[0]);
        assertEquals(testRefreshToken, tokens[1]);
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(refreshTokenService, times(1)).saveRefreshToken(any(RefreshToken.class));
    }

    @Test
    void 로그인_실패_비밀번호불일치() {
        //given
        String wrongPassword = "wrongPassword";
        when(memberRepository.findByLoginId(testMember.getLoginId())).thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches(wrongPassword, testMember.getPassword())).thenReturn(false);

        //when & then
        assertThrows(CustomException.class, () -> authService.login(testMember.getLoginId(), wrongPassword, true));
    }

    @Test
    void 로그인_실패_존재하지않는아이디() {
        //given
        when(memberRepository.findByLoginId("nonexistentUser")).thenReturn(Optional.empty());

        //when & then
        assertThrows(CustomException.class, () -> authService.login("nonexistentUser", "password123", true));
    }

    @Test
    void 로그아웃_성공() {
        //given
        doNothing().when(refreshTokenService).deleteAllRefreshTokenData(testRefreshToken);

        //when
        authService.logout(testRefreshToken);

        //then
        verify(refreshTokenService, times(1)).deleteAllRefreshTokenData(testRefreshToken);
    }

    @Test
    void 로그아웃_실패_토큰없음() {
        //when & then
        assertThrows(CustomException.class, () -> authService.logout(null));
    }

}

