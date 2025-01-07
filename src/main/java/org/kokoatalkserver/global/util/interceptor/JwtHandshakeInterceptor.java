package org.kokoatalkserver.global.util.interceptor;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtTokenizer jwtTokenizer;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.debug("Attempting WebSocket handshake for URI: {}", request.getURI());

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("Received Bearer Token: {}", token);

            if (jwtTokenizer.validateAccessToken(token)) {
                Claims claims = jwtTokenizer.parseAccessToken(token);
                Long userId = claims.get("id", Long.class);
                attributes.put("userId", userId);
                log.debug("Handshake successful for userId: {}", userId);
                return true;
            } else {
                log.error("Invalid token provided for WebSocket handshake.");
            }
        } else {
            log.error("Missing or invalid Authorization header for WebSocket handshake.");
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
