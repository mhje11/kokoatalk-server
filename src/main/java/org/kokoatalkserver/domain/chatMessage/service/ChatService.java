package org.kokoatalkserver.domain.chatMessage.service;

import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageSaveDto;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.config.chatConfig.RedisPublisher;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageService chatMessageService;
    private final MemberRepository memberRepository;
    private final RedisPublisher redisPublisher;

    public void sendMessage(Long kokoaId, String roomId, String message) {

        Optional<Member> memberOptional = memberRepository.findById(kokoaId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        ChatMessageSaveDto chatMessageSaveDto = ChatMessageSaveDto.createChatMessageSaveDto(roomId, String.valueOf(member.getKokoaId()), member.getNickname(), message);

        ChatMessageRedis chatMessageRedis = ChatMessageRedis.builder()
                .id(UUID.randomUUID().toString())
                .roomId(chatMessageSaveDto.getRoomId())
                .senderId(chatMessageSaveDto.getSenderId())
                .senderName(chatMessageSaveDto.getSenderNickname())
                .message(chatMessageSaveDto.getMessage())
                .created_at(LocalDateTime.now())
                .ttl(604800L)
                .build();

        chatMessageService.saveMessage(roomId, chatMessageRedis);

        ChannelTopic channelTopic = new ChannelTopic("chat.room." + roomId);
        redisPublisher.publish(channelTopic, chatMessageRedis);
    }
}
