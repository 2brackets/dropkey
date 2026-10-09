package io.github.twobrackets.dropkey.dto;

import java.time.Instant;

public record CreateDropResponse(
        String publicToken,
        String adminToken,
        Instant expiresAt
) {
}
