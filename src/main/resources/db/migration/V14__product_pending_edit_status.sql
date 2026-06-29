-- Allow the PENDING_EDIT status: a previously-published product whose vendor
-- edited it and is now awaiting admin re-approval. Hidden from customers until
-- the edit is approved (back to PUBLISHED) or rejected.
ALTER TABLE products DROP CONSTRAINT chk_product_status;

ALTER TABLE products ADD CONSTRAINT chk_product_status CHECK (status IN (
    'DRAFT', 'PENDING_REVIEW', 'PUBLISHED', 'PENDING_EDIT', 'REJECTED', 'ARCHIVED'
));
