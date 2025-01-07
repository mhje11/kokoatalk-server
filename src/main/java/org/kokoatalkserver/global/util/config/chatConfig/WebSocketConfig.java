package org.kokoatalkserver.global.util.config.chatConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.global.util.interceptor.JwtHandshakeInterceptor;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtTokenizer jwtTokenizer;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/api");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .addInterceptors(new JwtHandshakeInterceptor(jwtTokenizer))
//                .setAllowedOrigins("https://www.kokoatalk.shop", "https://api.kokoatalk.shop")
                .withSockJS();

        log.info("Websocket Endpoint '/ws' registered.");
    }
}
