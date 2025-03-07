package org.kokoatalkserver.global.util.jwt.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken implements Serializable {
    @Id
    private String kokoaId;

    @Indexed
    private String refresh;

    private Instant expiration;

    @TimeToLive
    private Long ttl;

    private RefreshToken(String kokoaId, String refresh, Long ttl) {
        this.kokoaId = kokoaId;
        this.refresh = refresh;
        this.expiration = Instant.now().plusSeconds(ttl);
        this.ttl = ttl;
    }

    public static RefreshToken create(String kokoaId, String refresh, Long ttl) {
        return new RefreshToken(kokoaId, refresh, ttl);
    }

    public boolean isExpired() {
        return this.expiration.isBefore(Instant.now());
    }
}
