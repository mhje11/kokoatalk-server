package org.kokoatalkserver.service.chatRoomTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kokoatalkserver.domain.ChatRoomParticipant.entity.ChatRoomParticipant;
import org.kokoatalkserver.domain.ChatRoomParticipant.repository.ChatRoomParticipantJdbcRepository;
import org.kokoatalkserver.domain.ChatRoomParticipant.repository.ChatRoomParticipantRepository;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomInfoDto;
import org.kokoatalkserver.domain.chatRoom.entity.ChatRoom;
import org.kokoatalkserver.domain.chatRoom.entity.ChatRoomType;
import org.kokoatalkserver.domain.chatRoom.repository.ChatRoomRepository;
import org.kokoatalkserver.domain.chatRoom.service.ChatRoomService;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomParticipantRepository chatRoomParticipantRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChatRoomParticipantJdbcRepository chatRoomParticipantJdbcRepository;

    private Member testMember;
    private Member friendMember;
    private ChatRoom chatRoom;

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

        ReflectionTestUtils.setField(testMember, "kokoaId", 1L);
        ReflectionTestUtils.setField(friendMember, "kokoaId", 2L);
        chatRoom = ChatRoom.createChatRoom("테스트 채팅방", ChatRoomType.PRIVATE);
    }

    @Test
    void 채팅방_생성_성공() {
        List<String> friendCodes = List.of(friendMember.getFriendCode());

        when(memberRepository.findByFriendCode(friendMember.getFriendCode())).thenReturn(Optional.of(friendMember));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        assertDoesNotThrow(() -> chatRoomService.createChatRoom("테스트 채팅방", friendCodes));
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
        verify(chatRoomParticipantRepository, times(1)).saveAll(anyList());
    }

    @Test
    void 채팅방_목록_조회_성공() {
        when(memberRepository.findByLoginId(testMember.getLoginId())).thenReturn(Optional.of(testMember));
        when(chatRoomParticipantRepository.findChatRoomsByMemberId(anyLong())).thenReturn(List.of());

        List<ChatRoomInfoDto> chatRooms = chatRoomService.getRoomList(testMember.getLoginId());

        assertNotNull(chatRooms);
        assertEquals(0, chatRooms.size());
    }

    @Test
    void 채팅방_나가기_성공() {
        ChatRoomParticipant chatRoomParticipant = ChatRoomParticipant.createChatRoomParticipant(chatRoom, testMember);

        when(memberRepository.findByLoginId(testMember.getLoginId())).thenReturn(Optional.of(testMember));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomParticipantRepository.findByChatRoomAndMember(chatRoom, testMember)).thenReturn(Optional.of(chatRoomParticipant));

        assertDoesNotThrow(() -> chatRoomService.leaveRoom(testMember.getLoginId(), chatRoom.getId()));

        verify(chatRoomParticipantRepository, times(1)).delete(chatRoomParticipant);
        verify(chatRoomRepository, times(1)).delete(chatRoom);
    }

    @Test
    void 개인채팅방_그룹채팅방으로_변경() {
        List<String> newFriends = List.of("newFriend1", "newFriend2");
        ChatRoomParticipant chatRoomParticipant = ChatRoomParticipant.createChatRoomParticipant(chatRoom, testMember);

        when(chatRoomParticipantRepository.findAllByChatRoom(chatRoom)).thenReturn(List.of(chatRoomParticipant));
        when(memberRepository.findByFriendCodeIn(newFriends)).thenReturn(List.of(friendMember));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        assertDoesNotThrow(() -> chatRoomService.createGroupChatRoomFromPrivate(chatRoom, newFriends));
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    void 그룹채팅방_멤버_추가() {
        ChatRoom groupChatRoom = ChatRoom.createChatRoom("그룹 채팅방", ChatRoomType.GROUP);
        List<String> newFriends = List.of("newFriend1", "newFriend2");

        when(memberRepository.findByFriendCodeIn(newFriends)).thenReturn(List.of(friendMember));
        when(chatRoomParticipantRepository.findAllByChatRoom(groupChatRoom)).thenReturn(List.of());

        assertDoesNotThrow(() -> chatRoomService.addMembersToGroupChatRoom(groupChatRoom, newFriends));
        verify(chatRoomParticipantJdbcRepository, times(1)).batchInsertParticipants(anyList());
    }

    @Test
    void 채팅방_타입_체크() {
        List<String> newFriends = List.of("newFriend1");
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        assertDoesNotThrow(() -> chatRoomService.checkRoomType(chatRoom.getId(), newFriends));
    }

}
