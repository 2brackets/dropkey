CREATE TABLE drops (
    id CHAR(36) NOT NULL,
    public_token VARCHAR(64) NOT NULL,
    admin_token VARCHAR(64) NOT NULL,
    password_hash VARCHAR(255) NULL,
    created_at TIMESTAMP(6) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    max_uploads INT NULL,
    max_file_size BIGINT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_drops_public_token (public_token),
    UNIQUE KEY uk_drops_admin_token (admin_token)
);