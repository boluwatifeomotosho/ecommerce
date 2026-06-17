CREATE TABLE phone_otp_sessions (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    phone         VARCHAR(20) UNIQUE NOT NULL,
    otp_hash      VARCHAR(64) NOT NULL,
    expires_at    TIMESTAMP   NOT NULL,
    attempt_count INT         NOT NULL DEFAULT 0,
    request_count INT         NOT NULL DEFAULT 0,
    window_start  TIMESTAMP   NOT NULL,
    created_at    TIMESTAMP   NOT NULL DEFAULT now()
);
