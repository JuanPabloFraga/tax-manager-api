-- ============================================================================
-- V4: Create vouchers table
-- ============================================================================

CREATE TABLE vouchers (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    taxpayer_id     UUID            NOT NULL,
    category        VARCHAR(10)     NOT NULL,
    voucher_type    VARCHAR(20)     NOT NULL,
    issue_date      DATE            NOT NULL,
    point_of_sale   INTEGER         NOT NULL,
    voucher_number  BIGINT          NOT NULL,
    net_amount      NUMERIC(19, 4)  NOT NULL,
    vat_amount      NUMERIC(19, 4)  NOT NULL,
    exempt_amount   NUMERIC(19, 4)  NOT NULL,
    total_amount    NUMERIC(19, 4)  NOT NULL,
    description     VARCHAR(500),
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now(),

    CONSTRAINT pk_vouchers PRIMARY KEY (id),
    CONSTRAINT fk_vouchers_taxpayer_id FOREIGN KEY (taxpayer_id)
        REFERENCES taxpayers (id) ON DELETE RESTRICT,
    CONSTRAINT ck_vouchers_category CHECK (category IN ('PURCHASE', 'SALE')),
    CONSTRAINT ck_vouchers_voucher_type CHECK (
        voucher_type IN (
            'FACTURA_A', 'FACTURA_B', 'FACTURA_C',
            'NOTA_CREDITO_A', 'NOTA_CREDITO_B', 'NOTA_CREDITO_C',
            'NOTA_DEBITO_A', 'NOTA_DEBITO_B', 'NOTA_DEBITO_C',
            'RECIBO', 'TICKET'
        )
    ),
    CONSTRAINT ck_vouchers_point_of_sale CHECK (point_of_sale BETWEEN 1 AND 99999),
    CONSTRAINT ck_vouchers_voucher_number CHECK (voucher_number > 0),
    CONSTRAINT ck_vouchers_net_amount CHECK (net_amount >= 0),
    CONSTRAINT ck_vouchers_vat_amount CHECK (vat_amount >= 0),
    CONSTRAINT ck_vouchers_exempt_amount CHECK (exempt_amount >= 0),
    CONSTRAINT ck_vouchers_total_amount CHECK (total_amount > 0)
);

CREATE INDEX idx_vouchers_taxpayer_id ON vouchers (taxpayer_id);
CREATE INDEX idx_vouchers_category_issue_date ON vouchers (category, issue_date);
CREATE INDEX idx_vouchers_issue_date ON vouchers (issue_date);
