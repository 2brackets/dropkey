package io.github.twobrackets.dropkey.service;

import io.github.twobrackets.dropkey.model.Drop;
import io.github.twobrackets.dropkey.model.DropAccessToken;
import io.github.twobrackets.dropkey.repository.DropAccessTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DropAccessServiceTest {

    @Mock
    private DropAccessTokenRepository repository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private DropAccessService dropAccessService;

    @Test
    void createAccessTokenShouldStoreHashInsteadOfRawToken() {
        Drop drop = new Drop();
        drop.setId(UUID.randomUUID());
        drop.setExpiresAt(Instant.now().plusSeconds(3600));

        when(tokenService.generateToken()).thenReturn("secret-token");

        var result = dropAccessService.createAccessToken(drop);

        var captor = org.mockito.ArgumentCaptor.forClass(DropAccessToken.class);
        verify(repository).save(captor.capture());

        DropAccessToken savedToken = captor.getValue();

        assertEquals("secret-token", result.token());
        assertNotEquals("secret-token", savedToken.getTokenHash());
        assertEquals(64, savedToken.getTokenHash().length());
        assertEquals(drop.getId().toString(), savedToken.getDropId());
        assertNotNull(savedToken.getId());
    }

    @Test
    void createAccessTokenShouldExpireAfter15Minutes() {
        Drop drop = new Drop();
        drop.setId(UUID.randomUUID());
        drop.setExpiresAt(Instant.now().plusSeconds(3600));

        when(tokenService.generateToken()).thenReturn("secret-token");

        Instant before = Instant.now();

        var result = dropAccessService.createAccessToken(drop);

        Instant after = Instant.now();

        assertFalse(result.expiresAt().isBefore(
                before.plusSeconds(15 * 60)
        ));

        assertFalse(result.expiresAt().isAfter(
                after.plusSeconds(15 * 60)
        ));
    }

    @Test
    void createAccessTokenShouldNotOutliveDrop() {
        Drop drop = new Drop();
        drop.setId(UUID.randomUUID());
        drop.setExpiresAt(Instant.now().plusSeconds(60));

        when(tokenService.generateToken()).thenReturn("secret-token");

        var result = dropAccessService.createAccessToken(drop);

        assertEquals(drop.getExpiresAt(), result.expiresAt());
    }
}