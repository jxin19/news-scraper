-- Member 테이블 생성
CREATE TABLE member
(
    member_id     BIGSERIAL PRIMARY KEY,
    username      VARCHAR(16)  NOT NULL,
    password      VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- updated_at 자동 업데이트를 위한 트리거
CREATE
    OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$
    language 'plpgsql';

CREATE TRIGGER update_member_updated_at
    BEFORE UPDATE
    ON member
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- 인덱스 생성
CREATE UNIQUE INDEX idx_member_username ON member (username);
CREATE INDEX idx_member_username_hash ON member USING HASH (username);
