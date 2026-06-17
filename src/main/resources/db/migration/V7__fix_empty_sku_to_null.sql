-- Convert empty-string SKUs to NULL so the unique constraint allows multiple products without a SKU
UPDATE products SET sku = NULL WHERE sku = '';
