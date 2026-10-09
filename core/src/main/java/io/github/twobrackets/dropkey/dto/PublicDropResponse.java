package io.github.twobrackets.dropkey.dto;

import java.time.Instant;

public record PublicDropResponse(
        boolean passwordRequired,
        Instant expiresAt
) {
}
