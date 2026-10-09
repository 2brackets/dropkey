package io.github.twobrackets.dropkey.repository;

import io.github.twobrackets.dropkey.model.Drop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DropRepository extends JpaRepository<Drop, UUID> {
    Optional<Drop> findByPublicToken(String publicToken);
}
