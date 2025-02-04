package org.kokoatalkserver.domain.chatMessage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageSaveDto;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.config.chatConfig.RedisPublisher;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageService chatMessageService;
    private final MemberRepository memberRepository;
    private final RedisPublisher redisPublisher;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> uploadFiles(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream()
                .map(this::uploadFileToTemp)
                .collect(Collectors.toList());
    }

    private String uploadFileToTemp(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException(ExceptionCode.INVALID_FILE_FORMAT);
        }
        String tempPath = "temp/" + createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()){
            amazonS3.putObject(new PutObjectRequest(bucket, tempPath, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.FILE_UPLOAD_FAILED);
        }
        return amazonS3.getUrl(bucket, tempPath).toString();
    }

    public List<String> moveFilesToFinalLocation(List<String> tempUrls) {
        return tempUrls.stream()
                .map(this::moveFileToFinalLocation)
                .collect(Collectors.toList());
    }

    private String moveFileToFinalLocation(String tempUrl) {
        String tempPath = tempUrl.substring(tempUrl.indexOf("/temp") + 1);
        String finalPath = tempPath.replace("temp/", "chat/");

        try {
            amazonS3.copyObject(bucket, tempPath, bucket, finalPath);
            amazonS3.deleteObject(bucket, tempPath);
        } catch (AmazonS3Exception e) {
            throw new CustomException(ExceptionCode.FILE_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(bucket, finalPath).toString();
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일 : " + fileName + " 입니다.");
        }
    }


    public void sendMessage(Long kokoaId, String roomId, String message, List<String> tempUrls) {

        Optional<Member> memberOptional = memberRepository.findById(kokoaId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        List<String> finalImageUrls = moveFilesToFinalLocation(tempUrls);

        ChatMessageSaveDto chatMessageSaveDto = ChatMessageSaveDto.createChatMessageSaveDto(roomId, String.valueOf(member.getKokoaId()), member.getNickname(), message, finalImageUrls);

        ChatMessageRedis chatMessageRedis = ChatMessageRedis.builder()
                .id(UUID.randomUUID().toString())
                .roomId(chatMessageSaveDto.getRoomId())
                .senderId(chatMessageSaveDto.getSenderId())
                .senderName(chatMessageSaveDto.getSenderNickname())
                .message(chatMessageSaveDto.getMessage())
                .created_at(LocalDateTime.now())
                .imageUrls(chatMessageSaveDto.getImageUrls())
                .ttl(604800L)
                .build();

        chatMessageService.saveMessage(roomId, chatMessageRedis);

        ChannelTopic channelTopic = new ChannelTopic("chat.room." + roomId);
        redisPublisher.publish(channelTopic, chatMessageRedis);
    }

}
