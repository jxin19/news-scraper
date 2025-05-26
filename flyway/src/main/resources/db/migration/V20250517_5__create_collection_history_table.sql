CREATE TABLE collection_history
(
    id              BIGSERIAL PRIMARY KEY,
    start_time      TIMESTAMP   NOT NULL,
    end_time        TIMESTAMP,
    status          VARCHAR(20) NOT NULL CHECK (status IN ('STARTED', 'COMPLETED', 'FAILED')),
    message         TEXT,
    total_collected INTEGER     NOT NULL DEFAULT 0
);

-- 인덱스 추가
CREATE INDEX idx_collection_history_status ON collection_history(status);
CREATE INDEX idx_collection_history_start_time ON collection_history(start_time DESC);
CREATE INDEX idx_collection_history_end_time ON collection_history(end_time);
