CREATE TABLE news_source
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(32)  NOT NULL UNIQUE,
    url        VARCHAR(255) NOT NULL,
    code       VARCHAR(16)  NOT NULL,
    type       VARCHAR(16)  NOT NULL, -- 'RSS' or 'SCRAPING' 구분
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 트리거 적용
CREATE TRIGGER update_news_source_updated_at
    BEFORE UPDATE
    ON news_source
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 인덱스 생성
CREATE INDEX idx_news_source_is_active ON news_source(is_active);
CREATE INDEX idx_news_source_url ON news_source(url);
CREATE INDEX idx_news_source_type ON news_source(type);
