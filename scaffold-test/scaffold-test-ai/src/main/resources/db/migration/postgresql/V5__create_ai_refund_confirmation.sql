CREATE TABLE IF NOT EXISTS ai_pending_action (
    id bigserial PRIMARY KEY,
    confirmation_id varchar(64) NOT NULL,
    user_id bigint NOT NULL,
    action varchar(16) NOT NULL,
    order_no varchar(32) NOT NULL,
    amount numeric(19, 2) NOT NULL,
    reason varchar(500) NOT NULL,
    summary varchar(1000) NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    status varchar(16) NOT NULL,
    executed_at timestamp with time zone,
    gmt_created timestamp NOT NULL,
    gmt_modified timestamp NOT NULL,
    created_by bigint,
    modified_by bigint,
    deleted integer NOT NULL DEFAULT 0,
    CONSTRAINT uk_ai_pending_confirmation UNIQUE (confirmation_id)
);

CREATE INDEX IF NOT EXISTS idx_ai_pending_user_status
    ON ai_pending_action (user_id, status, expires_at);

CREATE TABLE IF NOT EXISTS ai_refund_audit (
    id bigserial PRIMARY KEY,
    confirmation_id varchar(64) NOT NULL,
    user_id bigint NOT NULL,
    order_no varchar(32) NOT NULL,
    event varchar(16) NOT NULL,
    detail varchar(500) NOT NULL,
    gmt_created timestamp NOT NULL,
    gmt_modified timestamp NOT NULL,
    created_by bigint,
    modified_by bigint,
    deleted integer NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ai_refund_audit_confirmation
    ON ai_refund_audit (confirmation_id, gmt_created, id);
