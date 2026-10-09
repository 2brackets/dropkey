package io.github.twobrackets.dropkey.service;

import io.github.twobrackets.dropkey.model.Drop;
import io.github.twobrackets.dropkey.model.DropAccessToken;
import io.github.twobrackets.dropkey.repository.DropAccessTokenRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class DropAccessService {

    private final DropAccessTokenRepository dropAccessTokenRepository;
    private final TokenService tokenService;
    public record AccessTokenResult(
            String token,
            Instant expiresAt
    ) {
    }

    public DropAccessService(
            DropAccessTokenRepository dropAccessTokenRepository,
            TokenService tokenService
    ) {
        this.dropAccessTokenRepository = dropAccessTokenRepository;
        this.tokenService = tokenService;
    }

    public AccessTokenResult createAccessToken(Drop drop) {
        String token = tokenService.generateToken();
        Instant now = Instant.now();
        if (!drop.getExpiresAt().isAfter(now)) {
            throw new IllegalStateException("Drop has expired");
        }

        Instant expiresAt = now.plus(Duration.ofMinutes(15));

        if (expiresAt.isAfter(drop.getExpiresAt())) {
            expiresAt = drop.getExpiresAt();
        }

        DropAccessToken accessToken = new DropAccessToken();
        accessToken.setId(UUID.randomUUID());
        accessToken.setDropId(drop.getId().toString());
        accessToken.setTokenHash(hashToken(token));
        accessToken.setCreatedAt(now);
        accessToken.setExpiresAt(expiresAt);

        dropAccessTokenRepository.save(accessToken);

        return new AccessTokenResult(token, expiresAt);
    }

    public boolean validateAccessToken(Drop drop, String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Instant now = Instant.now();
        if (!drop.getExpiresAt().isAfter(now)) {
            return false;
        }
        return dropAccessTokenRepository.findByTokenHash(hashToken(token))
                .filter(accessToken ->
                        accessToken.getDropId().equals(drop.getId().toString()))
                .filter(accessToken ->
                        accessToken.getExpiresAt().isAfter(now))
                .isPresent();
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

}
