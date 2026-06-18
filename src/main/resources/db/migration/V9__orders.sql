CREATE TABLE orders (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id         UUID          NOT NULL REFERENCES users(id),
    status              VARCHAR(30)   NOT NULL DEFAULT 'PENDING_PAYMENT',

    subtotal            NUMERIC(12,2) NOT NULL,
    delivery_fee        NUMERIC(12,2) NOT NULL DEFAULT 0,
    total               NUMERIC(12,2) NOT NULL,

    -- Snapshot of shipping address at time of order
    shipping_name       VARCHAR(200)  NOT NULL,
    shipping_phone      VARCHAR(20)   NOT NULL,
    shipping_address    TEXT          NOT NULL,
    shipping_city       VARCHAR(100)  NOT NULL,
    shipping_state      VARCHAR(100)  NOT NULL,

    -- Paystack payment details
    payment_reference   VARCHAR(100)  UNIQUE,
    payment_channel     VARCHAR(50),
    paid_at             TIMESTAMP,

    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id      UUID          REFERENCES products(id) ON DELETE SET NULL,
    product_name    VARCHAR(300)  NOT NULL,
    vendor_name     VARCHAR(200),
    unit_price      NUMERIC(12,2) NOT NULL,
    quantity        INTEGER       NOT NULL CHECK (quantity > 0),
    line_total      NUMERIC(12,2) NOT NULL
);

CREATE INDEX idx_orders_customer   ON orders(customer_id);
CREATE INDEX idx_orders_status     ON orders(status);
CREATE INDEX idx_order_items_order ON order_items(order_id);
