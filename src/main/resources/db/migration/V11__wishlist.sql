CREATE TABLE wishlist_items (
    id          UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id  UUID      NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (customer_id, product_id)
);

CREATE INDEX idx_wishlist_customer ON wishlist_items(customer_id);
