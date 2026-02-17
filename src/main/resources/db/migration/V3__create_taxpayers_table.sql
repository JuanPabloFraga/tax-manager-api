-- ============================================================================
-- V3: Create taxpayers table
-- ============================================================================

CREATE TABLE taxpayers (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    business_name   VARCHAR(200)    NOT NULL,
    cuit            VARCHAR(11)     NOT NULL,
    tax_condition   VARCHAR(30)     NOT NULL,
    fiscal_address  VARCHAR(300)    NOT NULL,
    email           VARCHAR(150),
    phone           VARCHAR(30),
    active          BOOLEAN         NOT NULL DEFAULT true,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now(),

    CONSTRAINT pk_taxpayers PRIMARY KEY (id),
    CONSTRAINT uk_taxpayers_cuit UNIQUE (cuit),
    CONSTRAINT ck_taxpayers_cuit_length CHECK (LENGTH(cuit) = 11),
    CONSTRAINT ck_taxpayers_tax_condition CHECK (
        tax_condition IN (
            'RESPONSABLE_INSCRIPTO',
            'MONOTRIBUTISTA',
            'EXENTO',
            'NO_RESPONSABLE',
            'CONSUMIDOR_FINAL'
        )
    )
);

CREATE INDEX idx_taxpayers_business_name ON taxpayers (business_name);
CREATE INDEX idx_taxpayers_active ON taxpayers (active);
