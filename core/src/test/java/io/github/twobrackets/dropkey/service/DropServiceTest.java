package io.github.twobrackets.dropkey.service;

import io.github.twobrackets.dropkey.dto.CreateDropRequest;
import io.github.twobrackets.dropkey.model.Drop;
import io.github.twobrackets.dropkey.repository.DropRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DropServiceTest {

    @Mock
    private DropRepository dropRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DropAccessService dropAccessService;

    @InjectMocks
    private DropService dropService;

    @Test
    void createDropShouldGenerateTokensAndSetExpiration() {
        when(tokenService.generateToken())
                .thenReturn("public-token", "admin-token");

        when(dropRepository.save(any(Drop.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateDropRequest request = new CreateDropRequest(24, null);

        Drop drop = dropService.createDrop(request);

        assertNotNull(drop.getId());
        assertEquals("public-token", drop.getPublicToken());
        assertEquals("admin-token", drop.getAdminToken());
        assertNotNull(drop.getCreatedAt());
        assertEquals(
                drop.getCreatedAt().plusSeconds(24 * 60 * 60),
                drop.getExpiresAt()
        );
        assertNull(drop.getPasswordHash());
        verify(dropRepository).save(drop);
    }

    @Test
    void createDropShouldHashPassword() {
        when(tokenService.generateToken())
                .thenReturn("public-token", "admin-token");

        when(passwordEncoder.encode("Password123"))
                .thenReturn("hashed-password");

        when(dropRepository.save(any(Drop.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateDropRequest request = new CreateDropRequest(24, "Password123");

        Drop drop = dropService.createDrop(request);

        assertEquals("hashed-password", drop.getPasswordHash());
        assertNotEquals("Password123", drop.getPasswordHash());

        verify(passwordEncoder).encode("Password123");
        verify(dropRepository).save(drop);
    }

    @Test
    void authenticateDropShouldRejectInvalidPassword() {
        Drop drop = new Drop();
        drop.setPasswordHash("stored-hash");
        drop.setExpiresAt(Instant.now().plusSeconds(3600));

        when(dropRepository.findByPublicToken("public-token"))
                .thenReturn(Optional.of(drop));

        when(passwordEncoder.matches("wrong-password", "stored-hash"))
                .thenReturn(false);

        var result = dropService.authenticateDrop(
                "public-token",
                "wrong-password"
        );

        assertTrue(result.isEmpty());
        verify(dropAccessService, never())
                .createAccessToken(any(Drop.class));
    }
}
