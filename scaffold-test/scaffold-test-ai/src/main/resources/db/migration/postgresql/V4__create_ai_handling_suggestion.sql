CREATE TABLE IF NOT EXISTS ai_handling_suggestion (
    id bigserial PRIMARY KEY,
    work_order_id bigint NOT NULL,
    diagnosis text NOT NULL,
    sources_json text NOT NULL,
    evidence_json text NOT NULL,
    risk_level varchar(16) NOT NULL,
    manual_review_required boolean NOT NULL,
    gmt_created timestamp NOT NULL,
    gmt_modified timestamp NOT NULL,
    created_by bigint,
    modified_by bigint,
    deleted integer NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ai_suggestion_work_order_created
    ON ai_handling_suggestion (work_order_id, gmt_created DESC, id DESC);

CREATE TABLE IF NOT EXISTS ai_handling_suggestion_action (
    suggestion_id bigint NOT NULL,
    action_order integer NOT NULL,
    action varchar(1000) NOT NULL,
    PRIMARY KEY (suggestion_id, action_order),
    CONSTRAINT fk_ai_suggestion_action FOREIGN KEY (suggestion_id)
        REFERENCES ai_handling_suggestion (id) ON DELETE CASCADE
);
