CREATE TABLE platform_settings (
    key        VARCHAR(100) PRIMARY KEY,
    value      TEXT,
    label      VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

INSERT INTO platform_settings (key, value, label) VALUES
    ('platform.name',   'NaijaMart',          'Platform Name'),
    ('support.email',   'support@naija.mart',  'Support Email'),
    ('support.phone',   '+234 000 000 0000',   'Support Phone'),
    ('commission.rate', '10',                  'Commission Rate (%)');
