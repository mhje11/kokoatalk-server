package org.kokoatalkserver.domain.chatMessage.service;

import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageSaveDto;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageSendDto;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.config.chatConfig.ChatPublisher;
import org.kokoatalkserver.global.util.config.chatConfig.ChatSubscriber;
import org.kokoatalkserver.global.util.config.chatConfig.DynamicSubscriber;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final DynamicSubscriber dynamicSubscriber;
    private final ChatSubscriber chatSubscriber;
    private final ChatPublisher chatPublisher;
    private final ChatMessageService chatMessageService;
    private final MemberRepository memberRepository;

    public void joinRoom(String roomId) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(chatSubscriber, "handleMessage");
        dynamicSubscriber.subscribe(roomId, listenerAdapter);
    }

    public void leaveRoom(String roomId) {
        dynamicSubscriber.unsubscribe(roomId);
    }

    public void sendMessage(String accountId, String roomId, String message) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        ChatMessageSaveDto chatMessageSaveDto = ChatMessageSaveDto.createChatMessageSaveDto(roomId, member.getLoginId(), String.valueOf(member.getKokoaId()), message);

        ChatMessageRedis chatMessageRedis = ChatMessageRedis.builder()
                .id(UUID.randomUUID().toString())
                .roomId(chatMessageSaveDto.getRoomId())
                .senderId(chatMessageSaveDto.getSenderId())
                .senderName(chatMessageSaveDto.getSenderNickname())
                .message(chatMessageSaveDto.getMessage())
                .created_at(LocalDateTime.now())
                .build();

        chatMessageService.saveMessage(roomId, chatMessageRedis);
        chatPublisher.publishMessage(roomId, chatMessageRedis);
    }
}
