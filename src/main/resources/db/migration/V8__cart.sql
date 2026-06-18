CREATE TABLE cart_items (
    id          UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID      NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    product_id  UUID      NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity    INTEGER   NOT NULL CHECK (quantity > 0),
    added_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (customer_id, product_id)
);

CREATE INDEX idx_cart_items_customer ON cart_items(customer_id);
