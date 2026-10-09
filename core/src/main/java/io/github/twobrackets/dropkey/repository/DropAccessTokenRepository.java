package io.github.twobrackets.dropkey.repository;

import io.github.twobrackets.dropkey.model.DropAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DropAccessTokenRepository
        extends JpaRepository<DropAccessToken, UUID> {

    Optional<DropAccessToken> findByTokenHash(String tokenHash);
}
