package org.kokoatalkserver.chatTest;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Assertions;
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
import org.kokoatalkserver.global.util.config.chatConfig.RedisPublisher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private AmazonS3 amazonS3;

    @Mock
    ChatRoomRepository chatRoomRepository;

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

        ReflectionTestUtils.setField(chatService, "bucket", "test-bucket");
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
    void 파일_업로드_성공() throws MalformedURLException {
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/temp/test.png";
        String expectedFileName = "temp/" + validFile.getOriginalFilename();

        doAnswer(invocation -> {
            PutObjectRequest request = invocation.getArgument(0);
            System.out.println("Mock S3 Upload : " + request.getKey());
            return null;
        }).when(amazonS3).putObject(any(PutObjectRequest.class));

        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(java.net.URI.create(expectedUrl).toURL());

        List<String> resultUrls = chatService.uploadFiles(List.of(validFile));

        assertNotNull(resultUrls);
        assertFalse(resultUrls.isEmpty());
        assertEquals(expectedUrl, resultUrls.get(0));

        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
    }


}
