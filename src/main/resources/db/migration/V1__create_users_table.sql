-- ============================================================================
-- V1: Create users table
-- ============================================================================

CREATE TABLE users (
    id          UUID            NOT NULL DEFAULT gen_random_uuid(),
    email       VARCHAR(150)    NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    full_name   VARCHAR(150)    NOT NULL,
    role        VARCHAR(20)     NOT NULL,
    active      BOOLEAN         NOT NULL DEFAULT true,
    created_at  TIMESTAMP       NOT NULL DEFAULT now(),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'ACCOUNTANT', 'VIEWER'))
);
