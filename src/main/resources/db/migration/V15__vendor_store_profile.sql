-- Vendor-specific profile fields. NULL for customer accounts.
ALTER TABLE users
    ADD COLUMN store_name           VARCHAR(255),
    ADD COLUMN store_bio            TEXT,
    ADD COLUMN bank_name            VARCHAR(100),
    ADD COLUMN bank_account_number  VARCHAR(20),
    ADD COLUMN bank_account_name    VARCHAR(255);
