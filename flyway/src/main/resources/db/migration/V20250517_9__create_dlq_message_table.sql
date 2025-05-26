CREATE TABLE dlq_message
(
    id                 BIGSERIAL PRIMARY KEY,
    key                VARCHAR(255),
    original_topic     VARCHAR(255) NOT NULL,
    value              TEXT,
    exception_message  TEXT,
    original_partition INTEGER,
    original_offset    BIGINT,
    original_timestamp BIGINT,
    status             VARCHAR(10)  NOT NULL,
    retry_count        INTEGER      NOT NULL DEFAULT 0,
    transformed        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP    NOT NULL,
    retried_at         TIMESTAMP,
    processed_at       TIMESTAMP
);

-- 인덱스 추가
CREATE INDEX idx_dlq_message_status ON dlq_message (status);
CREATE INDEX idx_dlq_message_created_at ON dlq_message (created_at);
CREATE INDEX idx_dlq_message_original_topic ON dlq_message (original_topic);
