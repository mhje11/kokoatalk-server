package org.kokoatalkserver.domainTest.friend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kokoatalkserver.domain.friend.entity.Friend;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;

import static org.junit.jupiter.api.Assertions.*;

public class FriendDomainTest {

    @Test
    @DisplayName("Friend 엔티티가 정상적으로 생성된다.")
    void createFriendEntity_Success() {
        //given
        Member member1 = Member.builder()
                .loginId("user123")
                .password("password")
                .nickname("nickname1")
                .build();

        Member member2 = Member.builder()
                .loginId("user456")
                .password("password")
                .nickname("nickname2")
                .build();

        //when
        Friend friend = Friend.createFriendEntity(member1, member2);

        //then
        assertEquals(member1, friend.getMember());
        assertEquals(member2, friend.getFriend());
    }

    @Test
    @DisplayName("자기 자신을 친구 추가하려 하면 예외가 발생한다.")
    void validateFriendShip_Fail_WhenAddingSelf() {
        //given
        Member member = Member.builder()
                .loginId("user123")
                .password("password")
                .nickname("nickname")
                .build();

        //when

        //then
        CustomException exception = assertThrows(CustomException.class, () -> member.validateFriendShip(member));
        assertEquals(ExceptionCode.CANNOT_ADD_SELF, exception.getExceptionCode());
    }
}
