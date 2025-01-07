package org.kokoatalkserver.global.util.config.chatConfig;

import lombok.RequiredArgsConstructor;
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
                .setAllowedOrigins("https://www.kokoatalk.shop", "https://api.kokoatalk.shop")
                .addInterceptors(new JwtHandshakeInterceptor(jwtTokenizer))
                .withSockJS();
    }
}
