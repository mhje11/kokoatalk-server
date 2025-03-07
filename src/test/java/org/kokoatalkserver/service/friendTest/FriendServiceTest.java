package org.kokoatalkserver.service.friendTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kokoatalkserver.domain.friend.dto.FriendInfoDto;
import org.kokoatalkserver.domain.friend.entity.Friend;
import org.kokoatalkserver.domain.friend.repository.FriendRepository;
import org.kokoatalkserver.domain.friend.service.FriendService;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.entity.Role;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.jwt.service.CustomUserDetails;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTest {

    @InjectMocks
    private FriendService friendService;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member testMember;
    private Member friendMember;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .loginId("testUser")
                .password("password123")
                .nickname("테스트 유저")
                .build();

        friendMember = Member.builder()
                .loginId("friendUser")
                .password("password123")
                .nickname("친구 유저")
                .build();

        customUserDetails = new CustomUserDetails(testMember.getLoginId(), "MEMBER");
    }

    @Test
    void 친구_찾기_성공() {
        when(memberRepository.findByFriendCode(friendMember.getFriendCode()))
                .thenReturn(Optional.of(friendMember));

        FriendInfoDto result = friendService.findByFriendCode(friendMember.getFriendCode());
        assertNotNull(result);
        assertEquals(friendMember.getNickname(), result.getNickname());
    }

    @Test
    void 친구_추가_성공() {
        when(memberRepository.findByLoginId(testMember.getLoginId())).thenReturn(Optional.of(testMember));
        when(memberRepository.findByFriendCode(friendMember.getFriendCode())).thenReturn(Optional.of(friendMember));
        when(friendRepository.existsByMemberAndFriend(testMember, friendMember)).thenReturn(false);

        assertDoesNotThrow(() -> friendService.addFriend(customUserDetails, friendMember.getFriendCode()));
        verify(friendRepository, times(1)).save(any(Friend.class));
    }

    @Test
    void 친구_추가_실패_자기자신() {
        when(memberRepository.findByLoginId(testMember.getLoginId())).thenReturn(Optional.of(testMember));
        when(memberRepository.findByFriendCode(testMember.getFriendCode())).thenReturn(Optional.of(testMember));

        assertThrows(CustomException.class, () -> friendService.addFriend(customUserDetails, testMember.getFriendCode()));
    }

    @Test
    void 친구_추가_실패_이미_추가된_친구() {
        when(memberRepository.findByLoginId(testMember.getLoginId())).thenReturn(Optional.of(testMember));
        when(memberRepository.findByFriendCode(friendMember.getFriendCode())).thenReturn(Optional.of(friendMember));
        when(friendRepository.existsByMemberAndFriend(testMember, friendMember)).thenReturn(true);

        assertThrows(CustomException.class, () -> friendService.addFriend(customUserDetails, friendMember.getFriendCode()));
    }

    @Test
    void 친구_목록_조회_성공() {
        when(memberRepository.findByLoginId(testMember.getLoginId())).thenReturn(Optional.of(testMember));
        when(friendRepository.findFriendsWithMembers(testMember.getKokoaId()))
                .thenReturn(List.of(Friend.createFriendEntity(testMember, friendMember)));

        List<FriendInfoDto> friendList = friendService.getFriendList(customUserDetails);

        assertFalse(friendList.isEmpty());
        assertEquals(friendMember.getNickname(), friendList.get(0).getNickname());
    }


}
