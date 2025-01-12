package org.kokoatalkserver.global.util.interceptor;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtTokenizer jwtTokenizer;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("핸드쉐이크 인터셉터 탔음");
        log.info("Incoming Websocket request : {}", request.getURI());
        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwt = token.substring(7);
                Claims claims = jwtTokenizer.parseAccessToken(jwt);
                Long userId = claims.get("id", Long.class);
                log.info("user kokoaId : {}", userId);

                attributes.put("principal", new WebSocketPrincipal(String.valueOf(userId)));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        log.info("헤더 안넘어옴");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private static class WebSocketPrincipal implements Principal {
        private final String name;

        public WebSocketPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
