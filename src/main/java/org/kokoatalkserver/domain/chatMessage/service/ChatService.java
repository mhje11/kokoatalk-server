package org.kokoatalkserver.domain.chatMessage.service;

import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.chatMessage.entity.ChatMessageRedis;
import org.kokoatalkserver.global.util.config.chatConfig.ChatPublisher;
import org.kokoatalkserver.global.util.config.chatConfig.ChatSubscriber;
import org.kokoatalkserver.global.util.config.chatConfig.DynamicSubscriber;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final DynamicSubscriber dynamicSubscriber;
    private final ChatSubscriber chatSubscriber;
    private final ChatPublisher chatPublisher;

    public void joinRoom(String roomId) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(chatSubscriber, "handleMessage");
        dynamicSubscriber.subscribe(roomId, listenerAdapter);
    }

    public void leaveRoom(String roomId) {
        dynamicSubscriber.unsubscribe(roomId);
    }

    public void sendMessage(String roomId, ChatMessageRedis message) {
        chatPublisher.publishMessage(roomId, message);
    }
}
