-- ─────────────────────────────────────────────────────────────────
-- Warehouse agents + in-app notifications
--
-- * Adds IN_TRANSIT to the order flow between SHIPPED and DELIVERED.
-- * Adds assigned_agent_id FK on orders (nullable — only set once a
--   subsidiary/main admin hands the order over to a warehouse agent).
-- * Creates a platform-wide notifications table used by every role.
--
-- AGENT is a new user role that is global to the platform (not scoped
-- to any vendor company). Admins and subsidiary admins can invite
-- agents; agents themselves are shared across all subsidiaries.
-- ─────────────────────────────────────────────────────────────────

ALTER TABLE orders
    ADD COLUMN assigned_agent_id UUID REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX idx_orders_assigned_agent ON orders(assigned_agent_id);

CREATE TABLE notifications (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id UUID          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type         VARCHAR(50)   NOT NULL,
    title        VARCHAR(255)  NOT NULL,
    body         TEXT,
    url          VARCHAR(500),
    read_at      TIMESTAMP,
    created_at   TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE INDEX idx_notifications_recipient           ON notifications(recipient_id);
CREATE INDEX idx_notifications_recipient_unread    ON notifications(recipient_id) WHERE read_at IS NULL;
CREATE INDEX idx_notifications_recipient_created   ON notifications(recipient_id, created_at DESC);
