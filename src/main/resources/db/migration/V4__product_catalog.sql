-- ─────────────────────────────────────────────────────────────────
-- Categories  (self-referential tree, max 2-3 levels in practice)
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE categories (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    parent_id   UUID         REFERENCES categories(id) ON DELETE SET NULL,
    image_url   VARCHAR(500),
    active      BOOLEAN      NOT NULL DEFAULT true,
    sort_order  INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_categories_parent  ON categories(parent_id);
CREATE INDEX idx_categories_slug    ON categories(slug);
CREATE INDEX idx_categories_active  ON categories(active);

-- ─────────────────────────────────────────────────────────────────
-- Products
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE products (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_id        UUID          NOT NULL REFERENCES users(id),
    category_id      UUID          NOT NULL REFERENCES categories(id),
    name             VARCHAR(255)  NOT NULL,
    slug             VARCHAR(300)  NOT NULL UNIQUE,
    description      TEXT,
    price            NUMERIC(12,2) NOT NULL CHECK (price >= 0),
    compare_at_price NUMERIC(12,2)           CHECK (compare_at_price >= 0),
    stock_quantity   INT           NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    sku              VARCHAR(100)  UNIQUE,
    status           VARCHAR(30)   NOT NULL DEFAULT 'DRAFT',
    rejection_reason TEXT,
    weight_grams     INT                    CHECK (weight_grams > 0),
    published_at     TIMESTAMP,
    created_at       TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP     NOT NULL DEFAULT now(),

    CONSTRAINT chk_product_status CHECK (status IN (
        'DRAFT', 'PENDING_REVIEW', 'PUBLISHED', 'REJECTED', 'ARCHIVED'
    ))
);

CREATE INDEX idx_products_status    ON products(status);
CREATE INDEX idx_products_vendor    ON products(vendor_id);
CREATE INDEX idx_products_category  ON products(category_id);
CREATE INDEX idx_products_slug      ON products(slug);

-- ─────────────────────────────────────────────────────────────────
-- Product images  (ordered, one marked primary)
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE product_images (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID         NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url        VARCHAR(500) NOT NULL,
    alt_text   VARCHAR(255),
    sort_order INT          NOT NULL DEFAULT 0,
    is_primary BOOLEAN      NOT NULL DEFAULT false,
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_product_images_product ON product_images(product_id);
