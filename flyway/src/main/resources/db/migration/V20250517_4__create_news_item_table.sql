CREATE TABLE news_item
(
    id             BIGSERIAL PRIMARY KEY,
    keyword_id     BIGINT        NOT NULL,
    source_id      BIGINT        NOT NULL,
    title          VARCHAR(255)  NOT NULL,
    url            VARCHAR(1000) NOT NULL,
    content        TEXT,
    published_date TIMESTAMP,
    collected_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_news_item_keyword FOREIGN KEY (keyword_id) REFERENCES keyword (id) ON DELETE CASCADE,
    CONSTRAINT fk_news_item_source FOREIGN KEY (source_id) REFERENCES news_source (id) ON DELETE CASCADE
);

-- URL에 유니크 제약 추가 (중복 뉴스 방지)
CREATE UNIQUE INDEX idx_news_item_url ON news_item(url);
CREATE INDEX idx_news_item_keyword_id ON news_item(keyword_id);
CREATE INDEX idx_news_item_source_id ON news_item(source_id);
CREATE INDEX idx_news_item_published_date ON news_item(published_date);
CREATE INDEX idx_news_item_keyword_collected ON news_item(keyword_id, collected_at DESC);
CREATE INDEX idx_news_item_source_collected ON news_item(source_id, collected_at DESC);