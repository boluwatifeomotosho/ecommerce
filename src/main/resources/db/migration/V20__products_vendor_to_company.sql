-- ─────────────────────────────────────────────────────────────────
-- Re-point products.vendor_id from users(id) to vendors(id).
--
-- Every VENDOR user was backfilled into vendors (V19) with owner_id
-- pointing back to their user row. We drop the users FK first so the
-- UPDATE can rewrite each product's vendor_id to the corresponding
-- vendor company id, then re-add the FK targeting vendors(id).
-- ─────────────────────────────────────────────────────────────────

ALTER TABLE products DROP CONSTRAINT products_vendor_id_fkey;

UPDATE products p
SET vendor_id = v.id
FROM vendors v
WHERE p.vendor_id = v.owner_id;

ALTER TABLE products
    ADD CONSTRAINT products_vendor_id_fkey
    FOREIGN KEY (vendor_id) REFERENCES vendors(id);
