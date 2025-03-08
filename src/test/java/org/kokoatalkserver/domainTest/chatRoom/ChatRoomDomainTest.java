package org.kokoatalkserver.domainTest.chatRoom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kokoatalkserver.domain.ChatRoomParticipant.entity.ChatRoomParticipant;
import org.kokoatalkserver.domain.chatRoom.entity.ChatRoom;
import org.kokoatalkserver.domain.chatRoom.entity.ChatRoomType;
import org.kokoatalkserver.domain.member.entity.Member;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChatRoomDomainTest {

    @Test
    @DisplayName("채팅방이 정상적으로 생성된다.")
    void createChatRoom_Success() {
        //given
        String roomName = "친구들과의 대화";
        ChatRoomType chatRoomType = ChatRoomType.PRIVATE;

        //when
        ChatRoom chatRoom = ChatRoom.createChatRoom(roomName, chatRoomType);

        //then
        assertNotNull(chatRoom);
        assertEquals(roomName, chatRoom.getRoomName());
        assertEquals(chatRoomType, chatRoom.getChatRoomType());
    }

    @Test
    @DisplayName("1 : 1 채팅방이 그룹채팅방으로 변환된다.")
    void convertToGroupChat_Success() {
        //given
        ChatRoom privateChatRoom = ChatRoom.createChatRoom("1:1 채팅", ChatRoomType.PRIVATE);

        List<Member> participants = List.of(
                Member.builder().loginId("user1").nickname("철수").password("password").build(),
                Member.builder().loginId("user2").nickname("영희").password("password").build(),
                Member.builder().loginId("user3").nickname("민수").password("password").build()
        );

        //when
        ChatRoom groupChatRoom = privateChatRoom.convertToGroupChat(participants);

        //then
        assertNotNull(groupChatRoom);
        assertEquals(ChatRoomType.GROUP, groupChatRoom.getChatRoomType());
    }

    @Test
    @DisplayName("참가자 수가 3명 이하일 때, 그룹 채팅방 이름이 참가자 닉네임으로 생성된다.")
    void generateGroupChatRoomName_SmallGroup() {
        //given
        ChatRoom chatRoom = ChatRoom.createChatRoom("기본방", ChatRoomType.PRIVATE);

        List<Member> participants = List.of(
                Member.builder().loginId("user1").nickname("철수").password("password").build(),
                Member.builder().loginId("user2").nickname("영희").password("password").build(),
                Member.builder().loginId("user3").nickname("민수").password("password").build()
        );

        //when
        String roomName = chatRoom.convertToGroupChat(participants).getRoomName();

        //then
        assertEquals("철수, 영희, 민수", roomName);
    }

    @Test
    @DisplayName("참가자 수가 4명 이상일 때, 그룹 채팅방 이름이 '닉네임1, 닉네임2, 닉네임3 외 x명' 형태로 생성된다.")
    void generateGroupChatRoomName_LargeGroup() {
        //given
        ChatRoom chatRoom = ChatRoom.createChatRoom("기본방", ChatRoomType.PRIVATE);
        List<Member> participants = List.of(
                Member.builder().loginId("user1").nickname("철수").password("password").build(),
                Member.builder().loginId("user2").nickname("영희").password("password").build(),
                Member.builder().loginId("user3").nickname("민수").password("password").build(),
                Member.builder().loginId("user4").nickname("지수").password("password").build(),
                Member.builder().loginId("user5").nickname("나영").password("password").build()
        );

        //when
        String roomName = chatRoom.convertToGroupChat(participants).getRoomName();

        //then
        assertEquals("철수, 영희, 민수 외 2명", roomName);
    }

    @Test
    @DisplayName("채팅방에 남은 참가자가 없을 때, isEmpty()가 true를 반환")
    void isEmpty_WhenNoParticipants() {
        //given
        ChatRoom chatRoom = ChatRoom.createChatRoom("빈 채팅방", ChatRoomType.GROUP);
        List<ChatRoomParticipant> participants = List.of();

        //when
        boolean isEmpty = chatRoom.isEmpty(participants);

        //then
        assertTrue(isEmpty);
    }

    @Test
    @DisplayName("채팅방이 그룹 채팅방이면 isGroupChat()이 true를 반환한다.")
    void isGroupChat_WhenGroupChat() {
        //given
        ChatRoom chatRoom = ChatRoom.createChatRoom("그룹 채팅방", ChatRoomType.GROUP);

        //when

        //then
        assertTrue(chatRoom.isGroupChat());
    }

    @Test
    @DisplayName("채팅방이 1 : 1 채팅이면 isGroupChat()이 false 반환한다.")
    void isGroupChat_WhenPrivateChat() {
        //given
        ChatRoom chatRoom = ChatRoom.createChatRoom("1 : 1 채팅방", ChatRoomType.PRIVATE);

        //when

        //then
        assertFalse(chatRoom.isGroupChat());
    }

}
