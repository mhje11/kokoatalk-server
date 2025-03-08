package org.kokoatalkserver.domainTest.member;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;

import static org.junit.jupiter.api.Assertions.*;

public class MemberDomainTest {

    @Test
    @DisplayName("프로필 이미지를 업데이트하면 새로운 URL로 변경된다.")
    void updateProfileUrl_Success() {
        //given
        Member member = Member.builder()
                .loginId("user123")
                .password("password")
                .nickname("nickname")
                .build();

        String newProfileUrl = "https://new-profile-image.com/image.png";

        //when
        member.updateProfileUrl(newProfileUrl);

        //then
        assertEquals(newProfileUrl, member.getProfileUrl());
    }


    @Test
    @DisplayName("프로필 이미지 업데이트 시 빈 문자열을 입력하면 예외가 발생한다.")
    void updateProfileUrl_Fail_WhenBlank() {
        //given
        Member member = Member.builder()
                .loginId("user123")
                .password("password")
                .nickname("nickname")
                .build();
        //when

        //then
        assertThrows(IllegalArgumentException.class, () -> member.updateProfileUrl(""));
    }

    @Test
    @DisplayName("배경 이미지를 업데이트하면 새로운 URL로 변경된다.")
    void updateBackgroundUrl_Success() {
        //given
        Member member = Member.builder()
                .loginId("user123")
                .password("password")
                .nickname("nickname")
                .build();

        String newBackgroundUrl = "https://new-backgroud-image.com/image.png";

        //when
        member.updateBackgroundUrl(newBackgroundUrl);

        //then
        assertEquals(newBackgroundUrl, member.getBackgroundUrl());
    }

    @Test
    @DisplayName("배경 이미지 업데이트 시 빈 문자열을 입력하면 예외가 발생한다.")
    void updateBackgroundUrl_Fail_WhenBlank() {
        //given
        Member member = Member.builder()
                .loginId("user123")
                .password("password")
                .nickname("nickname")
                .build();

        //when

        //then
        assertThrows(IllegalArgumentException.class, () -> member.updateBackgroundUrl(""));
    }

    @Test
    @DisplayName("프로필 이미지를 기본 이미지로 변경할 수 있다.")
    void deleteProfileImage_Success() {
        //given
        Member member = Member.builder()
                .loginId("user123")
                .password("password")
                .nickname("nickname")
                .build();

        //when
        member.deleteProfileImage();

        //then
        assertEquals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_default_image.png", member.getProfileUrl());
    }

    @Test
    @DisplayName("배경 이미지를 기본 이미지로 변경할 수 있다.")
    void deleteBackgroundImage_Success() {
        //given
        Member member = Member.builder()
                .loginId("user123")
                .password("password")
                .nickname("nickname")
                .build();

        //when
        member.deleteBackgroundImage();

        //then
        assertEquals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_background.jpg", member.getBackgroundUrl());
    }


}
