package org.kokoatalkserver.global.util.jwt.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.Instant;

@Getter
@NoArgsConstructor
@RedisHash("refresh_token")
public class RefreshToken implements Serializable {
    @Id
    private String loginId;
    @Indexed
    private String refresh;
    private Instant expiration;

    @TimeToLive
    private Long ttl;

    public RefreshToken(String loginId, String refresh, Long ttl) {
        this.loginId = loginId;
        this.refresh = refresh;
        this.expiration = Instant.now().plusSeconds(ttl);
        this.ttl = ttl;
    }

    public boolean isExpired() {
        return this.expiration.isBefore(Instant.now());
    }
}
