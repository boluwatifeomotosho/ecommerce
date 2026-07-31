UPDATE platform_settings
SET value = 'Pinepetosan Marketplace', updated_at = now()
WHERE key = 'platform.name' AND value = 'NaijaMart';

UPDATE platform_settings
SET value = 'support@pinepetosan.com', updated_at = now()
WHERE key = 'support.email' AND value = 'support@naija.mart';
