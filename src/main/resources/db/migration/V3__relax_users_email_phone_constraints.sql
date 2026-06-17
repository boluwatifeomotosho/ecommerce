-- Allow phone-registered users who have no email, and email-registered users who have no phone.
-- Both columns remain UNIQUE where present; only the NOT NULL is dropped from email.
ALTER TABLE users
    ALTER COLUMN email DROP NOT NULL,
    ADD CONSTRAINT users_phone_unique UNIQUE (phone);
