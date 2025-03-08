package org.kokoatalkserver.service.chatTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kokoatalkserver.domain.chatMessage.dto.ChatMessageScrollDto;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.kokoatalkserver.domain.chatMessage.repository.ChatMessageMySqlRepository;
import org.kokoatalkserver.domain.chatMessage.service.ChatMessageService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceTest {

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Mock
    private RedisTemplate<String, ChatMessageRedis> redisTemplate;

    @Mock
    private ChatMessageMySqlRepository chatMessageMySqlRepository;

    @Mock
    private ListOperations<String, ChatMessageRedis> listOperations;

    private ChatMessageRedis testMessage;
    private String roomId = "123";

    @BeforeEach
    void setUp() {
        testMessage = ChatMessageRedis.builder()
                .id("434341")
                .roomId(roomId)
                .senderId("1")
                .senderName("테스트 유저")
                .message("안녕하세요")
                .created_at(LocalDateTime.now())
                .imageUrls(Collections.emptyList())
                .ttl(604800L)
                .build();
    }

    @Test
    void 메시지_저장_성공() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);

        chatMessageService.saveMessage(roomId, testMessage);

        verify(redisTemplate.opsForList(), times(1)).rightPush(anyString(), any(ChatMessageRedis.class));
        verify(redisTemplate, times(1)).expire(anyString(), anyLong(), any());
    }

    @Test
    void 메시지_조회_성공() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.range(anyString(), anyLong(), anyLong())).thenReturn(List.of(testMessage));

        List<ChatMessageScrollDto> messages = chatMessageService.getMessage(roomId, LocalDateTime.now(), 10);

        assertNotNull(messages);
        assertEquals(1, messages.size());
        assertEquals("안녕하세요", messages.get(0).getMessage());
    }

    @Test
    void 메시지_아카이빙_성공() {
        Set<String> keys = Set.of("chat:room:123");
        when(redisTemplate.keys(anyString())).thenReturn(keys);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.range(anyString(), anyLong(), anyLong())).thenReturn(List.of(testMessage));

        chatMessageService.archiveChatMessages();

        verify(chatMessageMySqlRepository, times(1)).saveAll(anyList());
    }
}
