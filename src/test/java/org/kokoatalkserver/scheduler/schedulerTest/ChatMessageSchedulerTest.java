package org.kokoatalkserver.scheduler.schedulerTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kokoatalkserver.domain.chatMessage.scheduler.ChatMessageScheduler;
import org.kokoatalkserver.domain.chatMessage.service.ChatMessageService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChatMessageSchedulerTest {

    @InjectMocks
    private ChatMessageScheduler chatMessageScheduler;

    @Mock
    private ChatMessageService chatMessageService;

    @Test
    void 백업_스케줄러_실행_테스트() {
        ReflectionTestUtils.invokeMethod(chatMessageScheduler, "backupChatMessages");

        verify(chatMessageService, times(1)).archiveChatMessages();
    }
}
