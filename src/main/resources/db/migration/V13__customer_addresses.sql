CREATE TABLE customer_addresses (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id    UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipient_name VARCHAR(255) NOT NULL,
    phone          VARCHAR(30)  NOT NULL,
    address_line   TEXT         NOT NULL,
    city           VARCHAR(100) NOT NULL,
    state          VARCHAR(100) NOT NULL,
    is_default     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customer_addresses_customer ON customer_addresses(customer_id);
