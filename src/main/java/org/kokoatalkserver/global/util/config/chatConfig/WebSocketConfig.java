package org.kokoatalkserver.global.util.config.chatConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.global.util.interceptor.AuthHandshakeInterceptor;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenizer jwtTokenizer;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new AuthHandshakeInterceptor(jwtTokenizer))
                .withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setSessionCookieNeeded(false)
                .setDisconnectDelay(30 * 1000)
                .setHeartbeatTime(25 * 1000);
    }

}
