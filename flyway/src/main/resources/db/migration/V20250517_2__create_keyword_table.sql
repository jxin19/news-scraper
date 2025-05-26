-- 키워드 테이블
CREATE TABLE keyword
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 트리거 적용
CREATE TRIGGER update_keyword_updated_at
    BEFORE UPDATE
    ON keyword
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 인덱스 생성
CREATE INDEX idx_keyword_is_active ON keyword(is_active);
CREATE INDEX idx_keyword_created_at ON keyword(created_at);
