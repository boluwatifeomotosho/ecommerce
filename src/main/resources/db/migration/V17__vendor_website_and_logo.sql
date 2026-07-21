-- Additional vendor onboarding fields: website URL and company logo.
ALTER TABLE users
    ADD COLUMN website_url       VARCHAR(500),
    ADD COLUMN company_logo_url  VARCHAR(500);
