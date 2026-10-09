package io.github.twobrackets.dropkey.dto;

import java.time.Instant;

public record VerifyDropPasswordResponse(
        String accessToken,
        Instant expiresAt
) {
}
