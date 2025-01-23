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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
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
            log.info("방삭제 : " + chatRoom.getRoomName());
            chatRoomRepository.delete(chatRoom);
            log.info("방삭제 성공");

        }
    }

    @Transactional
    public void createGroupChatRoomFromPrivate(ChatRoom privateChatRoom, List<String> newFriendCode) {
        List<ChatRoomParticipant> currentParticipants = chatRoomParticipantRepository.findAllByChatRoom(privateChatRoom);
        List<Member> currentMembers = currentParticipants.stream()
                .map(ChatRoomParticipant::getMember)
                .collect(Collectors.toList());

        List<Member> newMembers = newFriendCode.stream()
                .map(friendCode -> memberRepository.findByFriendCode(friendCode)
                        .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND)))
                .collect(Collectors.toList());

        List<Member> allMembers = Stream.concat(currentMembers.stream(), newMembers.stream())
                .distinct()
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for (Member member : allMembers) {
            sb.append(member.getNickname()).append(", ");
        }
        ChatRoom newGroupChatRoom = ChatRoom.createChatRoom(sb.toString().trim(), ChatRoomType.GROUP);
        chatRoomRepository.save(newGroupChatRoom);

        List<ChatRoomParticipant> newParticipants = allMembers.stream()
                .map(member -> ChatRoomParticipant.createChatRoomParticipant(newGroupChatRoom, member))
                .collect(Collectors.toList());
        chatRoomParticipantRepository.saveAll(newParticipants);
    }

    @Transactional
    public void addMembersToGroupChatRoom(ChatRoom groupChatRoom, List<String> newFriendCodes) {
        List<Member> newMembers = newFriendCodes.stream()
                .map(friendCode -> memberRepository.findByFriendCode(friendCode)
                        .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND)))
                .collect(Collectors.toList());

        List<Member> existingMembers = chatRoomParticipantRepository.findAllByChatRoom(groupChatRoom)
                .stream()
                .map(ChatRoomParticipant::getMember)
                .collect(Collectors.toList());

        List<ChatRoomParticipant> newParticipants = newMembers.stream()
                .filter(member -> !existingMembers.contains(member))
                .map(member -> ChatRoomParticipant.createChatRoomParticipant(groupChatRoom, member))
                .collect(Collectors.toList());

        chatRoomParticipantRepository.saveAll(newParticipants);
    }

    @Transactional
    public void checkRoomType(Long roomId, List<String> friendCodes) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND));
        if (ChatRoomType.GROUP.equals(chatRoom.getChatRoomType())) {
            addMembersToGroupChatRoom(chatRoom, friendCodes);
        } else {
            createGroupChatRoomFromPrivate(chatRoom, friendCodes);
        }
    }


}
