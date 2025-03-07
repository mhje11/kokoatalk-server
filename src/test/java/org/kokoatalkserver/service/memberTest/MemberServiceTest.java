package org.kokoatalkserver.service.memberTest;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kokoatalkserver.domain.member.Service.MemberService;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AmazonS3 amazonS3;

    private Member testMember;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .loginId("testUser")
                .password("password123")
                .nickname("테스트유저")
                .build();

        testMember.updateProfileUrl("https://test-bucket.s3.amazonaws.com/default_profile.png");
        testMember.updateBackgroundUrl("https://test-bucket.s3.amazonaws.com/default_background.png");

        mockFile = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                new byte[]{1, 2, 3, 4}
        );
        ReflectionTestUtils.setField(memberService, "bucket", "test-bucket");
    }

    @Test
    void 회원조회_성공() {
        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(testMember));

        Member result = memberService.findByLoginId("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getLoginId());
    }

    @Test
    void 회원조회_실패() {
        when(memberRepository.findByLoginId("wrongUser")).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> memberService.findByLoginId("wrongUser"));
    }

    @Test
    void 프로필이미지_업로드_성공() {

        try {
            when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(testMember));
            doReturn(new URL("https://test-bucket.s3.amazonaws.com/test.png"))
                    .when(amazonS3).getUrl(any(), any());

            assertDoesNotThrow(() -> memberService.uploadProfileImage(mockFile, "testUser"));
            verify(memberRepository, times(1)).save(any(Member.class));
        } catch (Exception e) {
            fail("예외가 발생하면 안됨: " + e.getMessage());
        }
    }

    @Test
    void 프로필이미지_업로드_실패_파일없음() {
        assertThrows(CustomException.class, () -> memberService.uploadProfileImage(null, "testUser"));
    }

    @Test
    void 배경이미지_업로드_성공() {

        try {
            when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(testMember));
            doReturn(new URL("https://test-bucket.s3.amazonaws.com/test.png"))
                    .when(amazonS3).getUrl(any(), any());

            assertDoesNotThrow(() -> memberService.uploadBackgroundImage(mockFile, "testUser"));
            verify(memberRepository, times(1)).save(any(Member.class));
        } catch (Exception e) {
            fail("예외가 발생하면 안됨 : " + e.getMessage());
        }
    }

    @Test
    void 배경이미지_업로드_실패_파일없음() {
        assertThrows(CustomException.class, () -> memberService.uploadBackgroundImage(null, "testUser"));
    }

    @Test
    void 프로필_이미지_삭제_성공() {

        testMember.updateProfileUrl("https://test-bucket.s3.amazonaws.com/testImagePf.png");
        System.out.println(testMember.getProfileUrl());

        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(testMember));

        assertDoesNotThrow(() -> memberService.deleteProfileImage("testUser"));
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void 프로필_이미지_삭제_실패_기본이미지() {
        testMember.updateProfileUrl("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_default_image.png");
        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(testMember));
        assertThrows(CustomException.class, () -> memberService.deleteProfileImage("testUser"));
    }

    @Test
    void 배경이미지_삭제_성공() {

        testMember.updateBackgroundUrl("https://test-bucket.s3.amazonaws.com/test_bg.png");
        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(testMember));

        assertDoesNotThrow(() -> memberService.deleteBackgroundImage("testUser"));
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void 배경이미지_삭제_실패_기본이미지() {
        testMember.updateBackgroundUrl("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_background.jpg");
        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(testMember));

        assertThrows(CustomException.class, () -> memberService.deleteBackgroundImage("testUser"));
    }

    @Test
    void 소개글_업데이트_성공() {
        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(testMember));

        assertDoesNotThrow(() -> memberService.updateBio("새로운 소개글", "testUser"));
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void 소개글_업데이트_실패_회원없음() {
        when(memberRepository.findByLoginId("wrongUser")).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> memberService.updateBio("새로운 소개글", "wrongUser"));
    }


}
