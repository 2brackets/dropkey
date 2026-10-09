package io.github.twobrackets.dropkey.service;

import io.github.twobrackets.dropkey.dto.CreateDropRequest;
import io.github.twobrackets.dropkey.model.Drop;
import io.github.twobrackets.dropkey.repository.DropRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class DropService {

    private final DropRepository dropRepository;
    private final DropAccessService dropAccessService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public DropService(DropRepository dropRepository, DropAccessService dropAccessService, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.dropRepository = dropRepository;
        this.dropAccessService = dropAccessService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public Drop createDrop(CreateDropRequest request) {
        Drop drop = new Drop();

        drop.setId(UUID.randomUUID());
        drop.setPublicToken(tokenService.generateToken());
        drop.setAdminToken(tokenService.generateToken());

        drop.setCreatedAt(Instant.now());
        drop.setExpiresAt(drop.getCreatedAt().plus(Duration.ofHours(request.expiresInHours())));

        if (request.password() != null && !request.password().isBlank()) {
            drop.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return dropRepository.save(drop);
    }

    public Optional<Drop> findActiveDrop(String publicToken) {
        return dropRepository.findByPublicToken(publicToken)
                .filter(drop -> drop.getExpiresAt().isAfter(Instant.now()));
    }

    public Optional<DropAccessService.AccessTokenResult> authenticateDrop(
            String publicToken,
            String password
    ) {
        return findActiveDrop(publicToken)
                .filter(drop -> drop.getPasswordHash() != null)
                .filter(drop -> passwordEncoder.matches(
                        password,
                        drop.getPasswordHash()
                ))
                .map(dropAccessService::createAccessToken);
    }
}
