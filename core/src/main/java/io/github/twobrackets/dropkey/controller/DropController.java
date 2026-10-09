package io.github.twobrackets.dropkey.controller;

import io.github.twobrackets.dropkey.dto.*;
import io.github.twobrackets.dropkey.model.Drop;
import io.github.twobrackets.dropkey.service.DropService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drops")
public class DropController {

    private final DropService dropService;

    public  DropController(DropService dropService) {
        this.dropService = dropService;
    }

    @GetMapping("/{publicToken}")
    public ResponseEntity<PublicDropResponse> getDrop(@PathVariable String publicToken) {
        return dropService.findActiveDrop(publicToken)
                .map(drop -> ResponseEntity.ok(
                        new PublicDropResponse(
                                drop.getPasswordHash() != null,
                                drop.getExpiresAt())
                )).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CreateDropResponse> createDrop(@Valid @RequestBody CreateDropRequest request) {
        Drop drop = dropService.createDrop(request);
        CreateDropResponse response = new CreateDropResponse(
                drop.getPublicToken(),
                drop.getAdminToken(),
                drop.getExpiresAt()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noCache())
                .body(response);
    }

    @PostMapping("/{publicToken}/verify-password")
    public ResponseEntity<VerifyDropPasswordResponse> verifyPassword(
            @PathVariable String publicToken,
            @Valid @RequestBody VerifyDropPasswordRequest request
    ) {
        return dropService.authenticateDrop(publicToken, request.password())
                .map(result -> ResponseEntity.ok()
                        .cacheControl(CacheControl.noStore())
                        .body(new VerifyDropPasswordResponse(
                                result.token(),
                                result.expiresAt()
                        )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
