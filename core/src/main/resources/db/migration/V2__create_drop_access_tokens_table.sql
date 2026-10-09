CREATE TABLE drop_access_tokens (
    id CHAR(36) NOT NULL,
    drop_id CHAR(36) NOT NULL,
    token_hash CHAR(64) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_drop_access_tokens_hash (token_hash),

    CONSTRAINT fk_drop_access_tokens_drop
    FOREIGN KEY (drop_id)
    REFERENCES drops(id)
    ON DELETE CASCADE
);