CREATE
MATERIALIZED VIEW news_item_mv AS
SELECT ni.id,
       ni.keyword_id,
       ni.source_id,
       ni.title,
       ni.url,
       ni.content,
       ni.published_date,
       ni.collected_at,
       k.name  as keyword_name,
       ns.name as source_name
FROM news_item ni
         INNER JOIN keyword k ON ni.keyword_id = k.id
         INNER JOIN news_source ns ON ni.source_id = ns.id;

-- 인덱스 생성
CREATE UNIQUE INDEX idx_news_item_mv_id ON news_item_mv (id);
CREATE INDEX idx_news_item_mv_url ON news_item_mv (url);
CREATE INDEX idx_news_item_mv_keyword_id ON news_item_mv (keyword_id);
CREATE INDEX idx_news_item_mv_source_id ON news_item_mv (source_id);
CREATE INDEX idx_news_item_mv_published_date ON news_item_mv (published_date);
CREATE INDEX idx_news_item_mv_keyword_collected ON news_item_mv (keyword_id, collected_at);
CREATE INDEX idx_news_item_mv_source_collected ON news_item_mv (source_id, collected_at);
CREATE INDEX idx_news_item_mv_keyword_name ON news_item_mv (keyword_name);
CREATE INDEX idx_news_item_mv_source_name ON news_item_mv (source_name);