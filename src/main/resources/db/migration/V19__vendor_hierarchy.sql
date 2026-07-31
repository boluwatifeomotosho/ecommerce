-- ─────────────────────────────────────────────────────────────────
-- Vendor tenant model (Phase 2a)
--
-- Introduces the Vendor (company) entity and its membership join table.
-- Every existing user with role='VENDOR' becomes a Vendor company with
-- themselves as the VENDOR_ADMIN member.
--
-- NOTE: products.vendor_id still points to users(id) at this stage.
-- Re-pointing to vendors(id) happens in a follow-up migration once
-- application code is switched over.
-- ─────────────────────────────────────────────────────────────────

CREATE TABLE vendors (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(255)  NOT NULL,
    slug                VARCHAR(300)  NOT NULL UNIQUE,
    owner_id            UUID          NOT NULL UNIQUE REFERENCES users(id),
    store_bio           TEXT,
    website_url         VARCHAR(500),
    company_logo_url    VARCHAR(500),
    bank_name           VARCHAR(100),
    bank_account_number VARCHAR(20),
    bank_account_name   VARCHAR(255),
    status              VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT now(),
    CONSTRAINT chk_vendor_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED'))
);

CREATE INDEX idx_vendors_owner  ON vendors(owner_id);
CREATE INDEX idx_vendors_status ON vendors(status);

CREATE TABLE vendor_members (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_id    UUID         NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    user_id      UUID         NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    member_role  VARCHAR(30)  NOT NULL,
    invited_by   UUID                  REFERENCES users(id) ON DELETE SET NULL,
    joined_at    TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT chk_vendor_member_role CHECK (member_role IN ('VENDOR_ADMIN', 'SUB_VENDOR'))
);

CREATE INDEX idx_vendor_members_vendor ON vendor_members(vendor_id);

-- Backfill: every existing VENDOR user becomes a Vendor company.
-- Slug format: lowercased alphanumerics from store_name, plus 8-char id suffix
-- for guaranteed uniqueness (store names may repeat or be blank).
INSERT INTO vendors (
    id, name, slug, owner_id,
    store_bio, website_url, company_logo_url,
    bank_name, bank_account_number, bank_account_name,
    status, created_at, updated_at
)
SELECT
    gen_random_uuid(),
    coalesce(nullif(store_name, ''), full_name, 'Vendor'),
    lower(
        regexp_replace(
            coalesce(nullif(store_name, ''), full_name, 'vendor'),
            '[^a-zA-Z0-9]+', '-', 'g'
        )
    ) || '-' || substring(id::text, 1, 8),
    id,
    store_bio, website_url, company_logo_url,
    bank_name, bank_account_number, bank_account_name,
    'ACTIVE', created_at, updated_at
FROM users
WHERE role = 'VENDOR';

-- Enroll each backfilled vendor's owner as VENDOR_ADMIN member.
INSERT INTO vendor_members (vendor_id, user_id, member_role, joined_at)
SELECT v.id, v.owner_id, 'VENDOR_ADMIN', v.created_at
FROM vendors v;

-- Attribution column so sub-vendors' work can be traced independently of
-- the company that owns the product.
ALTER TABLE products
    ADD COLUMN created_by UUID REFERENCES users(id);

UPDATE products SET created_by = vendor_id WHERE created_by IS NULL;

CREATE INDEX idx_products_created_by ON products(created_by);
