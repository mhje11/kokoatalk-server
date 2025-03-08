package org.kokoatalkserver.domain.chatMessage.service;

import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.kokoatalkserver.domain.chatRoom.entity.ChatRoom;
import org.kokoatalkserver.domain.chatRoom.repository.ChatRoomRepository;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.domain.s3.service.S3Service;
import org.kokoatalkserver.global.util.config.chatConfig.RedisPublisher;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageService chatMessageService;
    private final MemberRepository memberRepository;
    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final S3Service s3Service;


    public List<String> uploadFiles(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream()
                .map(s3Service::uploadFileToTemp)
                .collect(Collectors.toList());
    }


    public List<String> moveFilesToFinalLocation(List<String> tempUrls) {
        return tempUrls.stream()
                .map(s3Service::moveFileToFinalLocation)
                .collect(Collectors.toList());
    }


    public void sendMessage(Long kokoaId, String roomId, String message, List<String> tempUrls) {
        Optional<Member> memberOptional = memberRepository.findById(kokoaId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(Long.valueOf(roomId));
        if(chatRoomOptional.isEmpty()) {
            throw new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND);
        }
        List<String> finalImageUrls = moveFilesToFinalLocation(tempUrls);

        ChatMessageRedis chatMessageRedis = ChatMessageRedis.create(roomId,
                String.valueOf(member.getKokoaId()),
                member.getNickname(),
                message,
                finalImageUrls);

        chatMessageService.saveMessage(roomId, chatMessageRedis);

        ChannelTopic channelTopic = new ChannelTopic("chat.room." + roomId);
        redisPublisher.publish(channelTopic, chatMessageRedis);
    }

}
