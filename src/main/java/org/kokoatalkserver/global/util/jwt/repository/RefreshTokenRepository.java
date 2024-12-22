package org.kokoatalkserver.global.util.jwt.repository;

import org.kokoatalkserver.global.util.jwt.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByRefresh(String value);
    Optional<RefreshToken> findByKokoaId(String kokoaId);

    void deleteByRefresh(String refresh);
}
