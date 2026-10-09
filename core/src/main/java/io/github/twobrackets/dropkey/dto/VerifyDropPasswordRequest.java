package io.github.twobrackets.dropkey.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyDropPasswordRequest(
        @NotBlank String password
) {
}
