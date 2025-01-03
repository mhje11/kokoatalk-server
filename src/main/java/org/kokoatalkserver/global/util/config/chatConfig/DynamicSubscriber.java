package org.kokoatalkserver.global.util.config.chatConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DynamicSubscriber {
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final ConcurrentHashMap<String, PatternTopic> subscribedTopics = new ConcurrentHashMap<>();

    public void subscribe(String roomId, MessageListenerAdapter listenerAdapter) {
        String topicName = "chat:" + roomId;

        if (subscribedTopics.containsKey(topicName)) {
            return;
        }

        PatternTopic topic = new PatternTopic(topicName);
        redisMessageListenerContainer.addMessageListener(listenerAdapter, topic);
        subscribedTopics.put(topicName, topic);
    }

    public void unsubscribe(String roomId) {
        String topicName = "chat:" + roomId;

        PatternTopic topic = subscribedTopics.remove(topicName);
        if (topic != null) {
            redisMessageListenerContainer.removeMessageListener((message, pattern) -> {}, topic);
        }
    }

}
