ALTER TABLE products ADD COLUMN average_rating NUMERIC(3,2);
ALTER TABLE products ADD COLUMN review_count INTEGER NOT NULL DEFAULT 0;

CREATE TABLE reviews (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id  UUID          NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    customer_id UUID          NOT NULL REFERENCES users(id),
    order_id    UUID          NOT NULL REFERENCES orders(id),
    rating      INTEGER       NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment     TEXT,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    UNIQUE (product_id, customer_id)
);

CREATE INDEX idx_reviews_product  ON reviews(product_id);
CREATE INDEX idx_reviews_customer ON reviews(customer_id);
