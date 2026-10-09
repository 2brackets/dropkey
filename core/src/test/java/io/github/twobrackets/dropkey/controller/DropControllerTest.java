package io.github.twobrackets.dropkey.controller;

import io.github.twobrackets.dropkey.dto.PublicDropResponse;
import io.github.twobrackets.dropkey.model.Drop;
import io.github.twobrackets.dropkey.service.DropService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropControllerTest {

    @Mock
    private DropService dropService;

    @InjectMocks
    private DropController dropController;

    @Test
    void getDropShouldReturn404WhenDropDoesNotExist() {
        when(dropService.findActiveDrop("unknown-token"))
                .thenReturn(Optional.empty());

        var response = dropController.getDrop("unknown-token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getDropShouldReturnPublicInformation() {
        Drop drop = new Drop();
        drop.setPasswordHash("hashed-password");
        drop.setExpiresAt(Instant.now().plusSeconds(3600));
        drop.setAdminToken("secret-admin-token");

        when(dropService.findActiveDrop("public-token"))
                .thenReturn(Optional.of(drop));

        var response = dropController.getDrop("public-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        PublicDropResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.passwordRequired());
        assertEquals(drop.getExpiresAt(), body.expiresAt());
    }
}