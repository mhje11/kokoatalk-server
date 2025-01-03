package org.kokoatalkserver.domain.chatRoom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.ChatRoomParticipant.entity.ChatRoomParticipant;
import org.kokoatalkserver.domain.ChatRoomParticipant.repository.ChatRoomParticipantRepository;
import org.kokoatalkserver.domain.chatMessage.service.ChatService;
import org.kokoatalkserver.domain.chatRoom.dto.ChatRoomInfoDto;
import org.kokoatalkserver.domain.chatRoom.entity.ChatRoom;
import org.kokoatalkserver.domain.chatRoom.entity.ChatRoomType;
import org.kokoatalkserver.domain.chatRoom.repository.ChatRoomRepository;
import org.kokoatalkserver.domain.friend.dto.FriendInfoDto;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatService chatService;
    private final MemberRepository memberRepository;

    @Transactional
    public void createChatRoom(String roomName, List<String> friendCodeList) {
        ChatRoomType chatRoomType = friendCodeList.size() <= 2 ? ChatRoomType.PRIVATE : ChatRoomType.GROUP;

        ChatRoom chatRoom = ChatRoom.createChatRoom(roomName, chatRoomType);
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        List<Member> participantList = friendCodeList.stream()
                .map(friendCode -> memberRepository.findByFriendCode(friendCode).orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND)))
                .toList();

        List<ChatRoomParticipant> participants = participantList.stream()
                .map(member -> ChatRoomParticipant.createChatRoomParticipant(savedChatRoom, member))
                .collect(Collectors.toList());

        chatRoomParticipantRepository.saveAll(participants);

        chatService.joinRoom(String.valueOf(savedChatRoom.getId()));
    }

    public List<ChatRoomInfoDto> getRoomList(String accountId) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        List<ChatRoomParticipant> roomParticipantList = chatRoomParticipantRepository.findAllByMember(member);

        return roomParticipantList.stream()
                .map(participant -> {
                    ChatRoom chatRoom = participant.getChatRoom();
                    List<FriendInfoDto> participantInfoList = chatRoomParticipantRepository.findAllByChatRoom(chatRoom)
                            .stream()
                            .map(chatRoomParticipant -> FriendInfoDto.fromMember(chatRoomParticipant.getMember()))
                            .collect(Collectors.toList());
                    return ChatRoomInfoDto.createChatRoomInfoDto(chatRoom.getId(), chatRoom.getRoomName(), participantInfoList);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void leaveRoom(String accountId, Long roomId) {
        Member member = memberRepository.findByLoginId(accountId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND));

        ChatRoomParticipant chatRoomParticipant = chatRoomParticipantRepository.findByChatRoomAndMember(chatRoom, member)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND));

        chatRoomParticipantRepository.delete(chatRoomParticipant);

        // 남은 참가자 수 확인 및 방 삭제
        boolean hasParticipants = chatRoomParticipantRepository.existsByChatRoom(chatRoom);
        if (!hasParticipants) {
            chatService.leaveRoom(String.valueOf(roomId));
            log.info("방삭제 : " + chatRoom.getRoomName());
            chatRoomRepository.delete(chatRoom);
            log.info("방삭제 성공");

        }
    }


}
