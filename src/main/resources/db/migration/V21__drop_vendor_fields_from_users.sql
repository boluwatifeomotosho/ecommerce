-- ─────────────────────────────────────────────────────────────────
-- Storefront profile fields moved to `vendors` in V19; users no
-- longer holds them. Data was copied at that point, so dropping is
-- non-destructive.
-- ─────────────────────────────────────────────────────────────────

ALTER TABLE users
    DROP COLUMN store_name,
    DROP COLUMN store_bio,
    DROP COLUMN website_url,
    DROP COLUMN company_logo_url,
    DROP COLUMN bank_name,
    DROP COLUMN bank_account_number,
    DROP COLUMN bank_account_name;
