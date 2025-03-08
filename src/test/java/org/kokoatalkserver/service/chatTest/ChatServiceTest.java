package org.kokoatalkserver.service.chatTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.kokoatalkserver.domain.chatMessage.service.ChatMessageService;
import org.kokoatalkserver.domain.chatMessage.service.ChatService;
import org.kokoatalkserver.domain.chatRoom.entity.ChatRoom;
import org.kokoatalkserver.domain.chatRoom.repository.ChatRoomRepository;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.domain.s3.service.S3Service;
import org.kokoatalkserver.global.util.config.chatConfig.RedisPublisher;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RedisPublisher redisPublisher;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private S3Service s3Service;

    private Member testMember;
    private ChatRoom chatRoom;
    private MockMultipartFile validFile;
    private MockMultipartFile emptyFile;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .loginId("testUser")
                .password("password123")
                .nickname("테스트 유저")
                .build();

        validFile = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                new byte[]{1, 2, 3, 4, 5}
        );

        emptyFile = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[]{}
        );

        chatRoom = ChatRoom.createChatRoom("테스트 채팅방", null);
    }

    @Test
    void 메시지_전송_성공() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        when(chatRoomRepository.findById(anyLong())).thenReturn(Optional.of(chatRoom));

        assertDoesNotThrow(() -> chatService.sendMessage(1L, "123", "안녕하세요", List.of()));

        verify(chatMessageService, times(1)).saveMessage(anyString(), any(ChatMessageRedis.class));
        verify(redisPublisher, times(1)).publish(any(), any(ChatMessageRedis.class));
    }

    @Test
    void 파일_업로드_성공() {
        when(s3Service.uploadFileToTemp(any(MultipartFile.class)))
                .thenReturn("https://test-bucket.s3.amazonaws.com/temp/test.png");

        List<String> resultUrls = chatService.uploadFiles(List.of(validFile));

        assertNotNull(resultUrls);
        assertFalse(resultUrls.isEmpty());
        assertEquals("https://test-bucket.s3.amazonaws.com/temp/test.png", resultUrls.get(0));

        verify(s3Service, times(1)).uploadFileToTemp(any(MultipartFile.class));
    }

    @Test
    void 파일_업로드_실패_빈파일() {
        doThrow(new CustomException(ExceptionCode.INVALID_FILE_FORMAT))
                .when(s3Service).uploadFileToTemp(any(MultipartFile.class));
        assertThrows(CustomException.class,
                () -> chatService.uploadFiles(List.of(emptyFile)));

        verify(s3Service, times(1)).uploadFileToTemp(any(MultipartFile.class));
    }

    @Test
    void 파일_이동_성공() {
        List<String> tempUrls = List.of(
                "https://test-bucket.s3.amazonaws.com/temp/test1.png",
                "https://test-bucket.s3.amazonaws.com/temp/test2.png"
        );

        when(s3Service.moveFileToFinalLocation(anyString()))
                .thenAnswer(invocation -> {
                    String tempUrl = invocation.getArgument(0);
                    return tempUrl.replace("/temp/", "/chat/");
                });

        List<String> actualFinalUrls = chatService.moveFilesToFinalLocation(tempUrls);

        assertNotNull(actualFinalUrls);
        assertEquals(tempUrls.size(), actualFinalUrls.size());
        assertIterableEquals(List.of(
                "https://test-bucket.s3.amazonaws.com/chat/test1.png",
                "https://test-bucket.s3.amazonaws.com/chat/test2.png"
        ), actualFinalUrls);

        verify(s3Service, times(tempUrls.size())).moveFileToFinalLocation(anyString());
    }

    @Test
    void 파일_이동_실패_존재하지_않는_파일() {
        String tempUrl = "https://test-bucket.s3.amazonaws.com/temp/nonexistent.png";

        doThrow(new CustomException(ExceptionCode.FILE_UPLOAD_FAILED))
                .when(s3Service).moveFileToFinalLocation(anyString());

        assertThrows(CustomException.class, () -> chatService.moveFilesToFinalLocation(List.of(tempUrl)));

        verify(s3Service, times(1)).moveFileToFinalLocation(anyString());
    }
}
